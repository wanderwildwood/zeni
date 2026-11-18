package com.blockstream.data.usecases

import com.blockstream.data.crypto.GreenKeystore
import com.blockstream.data.crypto.PlatformCipher
import com.blockstream.data.data.CredentialType
import com.blockstream.data.data.EncryptedData
import com.blockstream.data.extensions.createLoginCredentials
import com.blockstream.data.usecases.DropFingerprintAfterNewEnrollmentUseCase.Companion.fingerprintCredentialsToDrop
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DropFingerprintAfterNewEnrollmentUseCaseTest {

    private val sealed = EncryptedData.fromByteArray(byteArrayOf(1, 2, 3), ByteArray(12))

    private fun credential(type: CredentialType) =
        createLoginCredentials(walletId = "w", network = "testnet-electrum", credentialType = type, encryptedData = sealed)

    @Test
    fun aWalletWithAFingerprintAndNoPinKeepsItsFingerprint() {
        assertTrue(fingerprintCredentialsToDrop(listOf(credential(CredentialType.BIOMETRICS_MNEMONIC))).isEmpty())
        assertTrue(fingerprintCredentialsToDrop(listOf(credential(CredentialType.BIOMETRICS_PINDATA))).isEmpty())
        assertTrue(fingerprintCredentialsToDrop(listOf(credential(CredentialType.BIOMETRICS_WATCHONLY_CREDENTIALS))).isEmpty())
    }

    @Test
    fun aWalletWithAPinLosesItsFingerprintAndKeepsItsPin() {
        val pin = credential(CredentialType.PIN_PINDATA)
        val fingerprint = credential(CredentialType.BIOMETRICS_MNEMONIC)
        val dropped = fingerprintCredentialsToDrop(listOf(pin, fingerprint))
        assertEquals(listOf(fingerprint), dropped)
    }

    @Test
    fun aPasswordCountsAsAPin() {
        val dropped = fingerprintCredentialsToDrop(
            listOf(credential(CredentialType.PASSWORD_PINDATA), credential(CredentialType.BIOMETRICS_PINDATA))
        )
        assertEquals(listOf(CredentialType.BIOMETRICS_PINDATA), dropped.map { it.credential_type })
    }

    @Test
    fun aPinThatHasUsedUpItsTriesDoesNotCount() {
        val spentPin = credential(CredentialType.PIN_PINDATA).copy(counter = 3)
        assertTrue(fingerprintCredentialsToDrop(listOf(spentPin, credential(CredentialType.BIOMETRICS_MNEMONIC))).isEmpty())
    }

    @Test
    fun onlyFingerprintCredentialsAreDropped() {
        val list = listOf(
            credential(CredentialType.PIN_PINDATA),
            credential(CredentialType.KEYSTORE_LIGHTNING_MNEMONIC),
            credential(CredentialType.KEYSTORE_BOLTZ_MNEMONIC),
            credential(CredentialType.RICH_WATCH_ONLY),
        )
        assertTrue(fingerprintCredentialsToDrop(list).isEmpty())
    }

    @Test
    fun aKeystoreThatCannotTellNeverReportsANewFingerprint() {
        val keystore = object : GreenKeystore {
            override fun encryptData(dataToEncrypt: ByteArray) = sealed
            override fun encryptData(cipher: PlatformCipher, dataToEncrypt: ByteArray) = sealed
            override fun decryptData(encryptedData: EncryptedData) = ByteArray(0)
            override fun decryptData(cipher: PlatformCipher, encryptedData: EncryptedData) = ByteArray(0)
            override fun canUseBiometrics() = true
            override fun isBiometricEnrolled() = true
        }
        assertFalse(keystore.biometricEnrollmentChanged())
    }
}
