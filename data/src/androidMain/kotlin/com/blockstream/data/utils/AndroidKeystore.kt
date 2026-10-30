package com.blockstream.data.utils

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import androidx.annotation.VisibleForTesting
import androidx.biometric.BiometricManager
import com.blockstream.data.crypto.GreenKeystore
import com.blockstream.data.crypto.KeystoreInvalidatedException
import com.blockstream.data.crypto.PlatformCipher
import com.blockstream.data.data.EncryptedData
import com.blockstream.utils.Loggable
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.UnrecoverableKeyException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec

class AndroidKeystore(val context: Context) : GreenKeystore {
    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }
    }

    fun isBiometricsAuthenticationRequired(): Boolean {
        try {
            getEncryptionCipher(if (keyStoreKeyExists(BIOMETRICS_GCM_ALIAS)) BIOMETRICS_GCM_ALIAS else BIOMETRICS_KEYSTORE_ALIAS)
        } catch (e: UserNotAuthenticatedException) {
            logger.i { "User not Authenticated, probably device was unlocked with Face biometrics" }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun biometricsKeyExists() =
        keyStoreKeyExists(BIOMETRICS_GCM_ALIAS) || keyStoreKeyExists(BIOMETRICS_KEYSTORE_ALIAS)

    /** Whether the key that sealed this particular data is still in the keystore. */
    fun biometricsKeyExists(encryptedData: EncryptedData) =
        keyStoreKeyExists(if (encryptedData.isGcm()) BIOMETRICS_GCM_ALIAS else BIOMETRICS_KEYSTORE_ALIAS)

    private fun keyStoreKeyExists(keystoreAlias: String): Boolean {
        try {
            return keyStore.containsAlias(keystoreAlias)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun deleteFromKeyStore(keystoreAlias: String) {
        try {
            // remove keystore alias
            keyStore.deleteEntry(keystoreAlias)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isKeyStoreValid(keystoreAlias: String): Boolean {
        return try {
            getEncryptionCipher(keystoreAlias)
            true
        } catch (e: Exception) {
            logger.e(e) { "Keystore validity check failed for $keystoreAlias" }
            keyRemainsValid(e)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    @Throws(Exception::class)
    fun initializeKeyStoreKey(keystoreAlias: String, isBiometric: Boolean) {
        if (keyStoreKeyExists(keystoreAlias)) {
            throw KeyStoreException("KeyStore is already created for $keystoreAlias")
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val builder = KeyGenParameterSpec.Builder(
            keystoreAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(256)

        if (isGcmAlias(keystoreAlias)) {
            // Authenticated encryption: a changed byte is refused before any plaintext exists.
            builder.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        } else {
            // Kept only so that data written before 0.1.3 can be read once and re-sealed.
            builder.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        }

        if (isBiometric) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                builder.setUnlockedDeviceRequired(true)
            }

            builder.setUserAuthenticationRequired(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                builder.setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG)
            } else {
                @Suppress("DEPRECATION")
                builder.setUserAuthenticationValidityDurationSeconds(-1)
            }

            builder.setInvalidatedByBiometricEnrollment(false)
        }

        keyGenerator.init(builder.build())

        // Add it to KeyStore
        keyGenerator.generateKey()
    }

    @Throws(Exception::class)
    private fun getEncryptionCipher(keystoreAlias: String): Cipher {
        val cipher = Cipher.getInstance(if (isGcmAlias(keystoreAlias)) GCM_TRANSFORMATION else TRANSFORMATION)
        val key = keyStore.getKey(keystoreAlias, null) as SecretKey
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher
    }

    @Throws(Exception::class)
    private fun getDecryptionCipher(keystoreAlias: String, encryptedData: EncryptedData): Cipher {
        // Data is read with the mode it was written in, told by the length of its IV.
        val gcm = encryptedData.isGcm()
        val alias = if (gcm) gcmAliasOf(keystoreAlias) else keystoreAlias
        val cipher = Cipher.getInstance(if (gcm) GCM_TRANSFORMATION else TRANSFORMATION)
        val key = keyStore.getKey(alias, null)
        cipher.init(
            Cipher.DECRYPT_MODE,
            key,
            if (gcm) GCMParameterSpec(GCM_TAG_BITS, encryptedData.getIv()) else IvParameterSpec(encryptedData.getIv())
        )
        return cipher
    }

    /**
     * The Key for Biometric use can be invalidated if a new fingerprint is enrolled as the key is
     * instantiated with {@code setInvalidatedByBiometricEnrollment} option enabled.
     * Recreate the keystore key if the key is no longer valid.
     *
     * The returned {@code Cipher} object must be unlocked by {@code BiometricPrompt} before use.
     */
    @Throws(Exception::class)
    fun getBiometricsEncryptionCipher(recreateKeyIfNeeded: Boolean): Cipher {
        if ((!keyStoreKeyExists(BIOMETRICS_GCM_ALIAS) || !isKeyStoreValid(
                BIOMETRICS_GCM_ALIAS
            )) && recreateKeyIfNeeded
        ) {
            deleteFromKeyStore(BIOMETRICS_GCM_ALIAS)
            initializeKeyStoreKey(BIOMETRICS_GCM_ALIAS, true)
        }

        return getEncryptionCipher(BIOMETRICS_GCM_ALIAS)
    }

    /**
     * In most cases @{code keystoreAlias} should be set to null to use the default key @{code BIOMETRICS_KEYSTORE_ALIAS}.
     * If you want to decrypt data from a different KeyStore (eg. migrated data) and the same decryption settings,
     * you can provide the alias to be used.
     */
    @Throws(Exception::class)
    fun getBiometricsDecryptionCipher(
        encryptedData: EncryptedData,
        keystoreAlias: String? = null
    ): Cipher {
        return getDecryptionCipher(keystoreAlias ?: BIOMETRICS_KEYSTORE_ALIAS, encryptedData)
    }

    @Throws(Exception::class)
    override fun encryptData(dataToEncrypt: ByteArray): EncryptedData {
        if (!keyStoreKeyExists(BASIC_GCM_ALIAS)) {
            initializeKeyStoreKey(BASIC_GCM_ALIAS, false)
        }

        val cipher = getEncryptionCipher(BASIC_GCM_ALIAS)

        return encryptData(cipher, dataToEncrypt)
    }

    @Throws(Exception::class)
    fun encryptData(cipher: Cipher, dataToEncrypt: ByteArray): EncryptedData {
        return EncryptedData.fromByteArray(cipher.doFinal(dataToEncrypt), cipher.iv)
    }

    override fun encryptData(cipher: PlatformCipher, dataToEncrypt: ByteArray): EncryptedData {
        return encryptData(cipher as Cipher, dataToEncrypt)
    }

    @Throws(Exception::class)
    override fun decryptData(encryptedData: EncryptedData): ByteArray {
        if (!keyStoreKeyExists(if (encryptedData.isGcm()) BASIC_GCM_ALIAS else BASIC_KEYSTORE_ALIAS)) {
            throw KeyStoreException("KeyStore Keys are not created. You have to init them first.")
        }

        val cipher = getDecryptionCipher(BASIC_KEYSTORE_ALIAS, encryptedData)

        return decryptData(cipher, encryptedData)
    }

    @Throws(Exception::class)
    override fun decryptData(cipher: PlatformCipher, encryptedData: EncryptedData): ByteArray {
        try {
            return (cipher as Cipher).doFinal(encryptedData.getEncryptedData())
        } catch (e: BadPaddingException) {
            throw KeystoreInvalidatedException("Keystore is invalided.")
        }
    }

    override fun needsResealing(encryptedData: EncryptedData): Boolean = !encryptedData.isGcm()

    override fun canUseBiometrics(): Boolean {
        val kManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        try {
            if (kManager.isDeviceSecure) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun isBiometricEnrolled(): Boolean {
        return try {
            val biometricManager = BiometricManager.from(context)
            val canAuthenticate = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    companion object : Loggable() {
        /**
         * Whether a key that raised [exception] while creating a cipher is still considered
         * valid. The biometrics key is shared by every wallet's biometric credentials, so
         * reporting it invalid leads to it being deleted and recreated, permanently
         * invalidating all of them. Only exceptions that provably mean the key is unusable
         * report it as invalid; anything unrecognised retains the key, as the error still
         * surfaces to the caller when the cipher is actually created.
         */
        @VisibleForTesting
        fun keyRemainsValid(exception: Exception): Boolean = when (exception) {
            is KeyPermanentlyInvalidatedException -> false
            is UnrecoverableKeyException -> false
            // The key exists but requires user authentication (eg. device was unlocked with face)
            is UserNotAuthenticatedException -> true
            else -> true
        }

        private const val TRANSFORMATION =
            "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
        private const val GCM_TRANSFORMATION =
            "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
        private const val GCM_TAG_BITS = 128
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"

        // v1: AES-CBC, written by versions up to 0.1.2. Read-only, for the one-time re-seal.
        const val BASIC_KEYSTORE_ALIAS = "v1-basic"
        const val BIOMETRICS_KEYSTORE_ALIAS = "v1-biometrics"

        // v2: AES-256-GCM, used for everything written since 0.1.3.
        const val BASIC_GCM_ALIAS = "v2-basic"
        const val BIOMETRICS_GCM_ALIAS = "v2-biometrics"

        private fun isGcmAlias(alias: String) = alias.startsWith("v2-")
        private fun gcmAliasOf(alias: String) = when (alias) {
            BASIC_KEYSTORE_ALIAS -> BASIC_GCM_ALIAS
            BIOMETRICS_KEYSTORE_ALIAS -> BIOMETRICS_GCM_ALIAS
            else -> alias
        }
    }
}