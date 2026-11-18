package com.blockstream.data.crypto

import com.blockstream.data.data.EncryptedData

typealias PlatformCipher = Any

class KeystoreInvalidatedException constructor(message: String) : Exception(message)

class BiometricsException constructor(message: String) : Exception(message)

interface GreenKeystore {
    fun encryptData(dataToEncrypt: ByteArray): EncryptedData
    fun encryptData(cipher: PlatformCipher, dataToEncrypt: ByteArray): EncryptedData
    fun decryptData(encryptedData: EncryptedData): ByteArray
    fun decryptData(cipher: PlatformCipher, encryptedData: EncryptedData): ByteArray
    fun canUseBiometrics(): Boolean
    fun isBiometricEnrolled(): Boolean

    /** Whether this data was sealed by the pre-0.1.3 AES-CBC key and should be re-sealed. */
    fun needsResealing(encryptedData: EncryptedData): Boolean = false

    /**
     * Whether a fingerprint was added to the phone (or every one removed) since the last
     * [acknowledgeBiometricEnrollment]. False whenever it cannot be told.
     */
    fun biometricEnrollmentChanged(): Boolean = false

    /** The change [biometricEnrollmentChanged] reported has been dealt with. */
    fun acknowledgeBiometricEnrollment() {}
}