package com.blockstream.data.usecases

import com.blockstream.data.crypto.GreenKeystore
import com.blockstream.data.crypto.PlatformCipher
import com.blockstream.data.data.CredentialType
import com.blockstream.data.data.EncryptedData
import com.blockstream.data.extensions.createLoginCredentials
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ResealKeystoreDataUseCaseTest {

    private val keystore = object : GreenKeystore {
        override fun encryptData(dataToEncrypt: ByteArray) = EncryptedData.fromByteArray(dataToEncrypt, ByteArray(12))
        override fun encryptData(cipher: PlatformCipher, dataToEncrypt: ByteArray) = encryptData(dataToEncrypt)
        override fun decryptData(encryptedData: EncryptedData) = encryptedData.getEncryptedData()
        override fun decryptData(cipher: PlatformCipher, encryptedData: EncryptedData) = encryptedData.getEncryptedData()
        override fun canUseBiometrics() = false
        override fun isBiometricEnrolled() = false
        override fun needsResealing(encryptedData: EncryptedData) = !encryptedData.isGcm()
    }

    private val legacy = EncryptedData.fromByteArray(byteArrayOf(1, 2, 3), ByteArray(16))
    private val gcm = EncryptedData.fromByteArray(byteArrayOf(1, 2, 3), ByteArray(12))

    private fun credential(type: CredentialType, data: EncryptedData?) =
        createLoginCredentials(walletId = "w", network = "testnet-electrum", credentialType = type, encryptedData = data)

    @Test
    fun nonceLengthTellsGcmFromCbc() {
        assertTrue(gcm.isGcm())
        assertFalse(legacy.isGcm())
    }

    @Test
    fun legacyKeystoreCredentialsAreResealed() {
        val list = listOf(
            credential(CredentialType.KEYSTORE_WATCHONLY_CREDENTIALS, legacy),
            credential(CredentialType.RICH_WATCH_ONLY, legacy),
        )
        assertEquals(2, ResealKeystoreDataUseCase.credentialsToReseal(list, keystore).size)
    }

    @Test
    fun gcmAndEmptyCredentialsAreLeftAlone() {
        val list = listOf(
            credential(CredentialType.KEYSTORE_WATCHONLY_CREDENTIALS, gcm),
            credential(CredentialType.PIN_PINDATA, null),
        )
        assertTrue(ResealKeystoreDataUseCase.credentialsToReseal(list, keystore).isEmpty())
    }

    @Test
    fun fingerprintCredentialsAreNeverTouchedWithoutAFinger() {
        val list = listOf(
            credential(CredentialType.BIOMETRICS_PINDATA, legacy),
            credential(CredentialType.BIOMETRICS_MNEMONIC, legacy),
            credential(CredentialType.BIOMETRICS_WATCHONLY_CREDENTIALS, legacy),
        )
        assertTrue(ResealKeystoreDataUseCase.credentialsToReseal(list, keystore).isEmpty())
    }
}
