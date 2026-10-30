package com.blockstream.data.usecases

import com.blockstream.data.crypto.GreenKeystore
import com.blockstream.data.data.CredentialType
import com.blockstream.data.database.Database
import com.blockstream.data.database.wallet.LoginCredentials
import com.blockstream.utils.Loggable

/**
 * Re-seals what versions up to 0.1.2 stored with the phone's AES-CBC key under AES-GCM, once.
 *
 * Only the credentials that need no fingerprint to open are touched here. Those sealed to the
 * fingerprint (BIOMETRICS_*) cannot be opened, let alone sealed again, without the person
 * placing a finger; they keep opening with their old key and are written in GCM the next time
 * fingerprint unlock is set up.
 */
class ResealKeystoreDataUseCase(
    private val database: Database,
    private val greenKeystore: GreenKeystore,
) {

    suspend operator fun invoke(): Int {
        var resealed = 0
        for (wallet in database.getAllWallets()) {
            val credentials = database.getLoginCredentials(wallet.id)
            for (credential in credentialsToReseal(credentials, greenKeystore)) {
                try {
                    val plain = greenKeystore.decryptData(credential.encrypted_data!!)
                    try {
                        database.replaceLoginCredential(
                            credential.copy(encrypted_data = greenKeystore.encryptData(plain))
                        )
                        resealed++
                    } finally {
                        plain.fill(0)
                    }
                } catch (e: Exception) {
                    // Left as it was: it still opens with the old key. Tried again next start.
                    logger.w { "Could not re-seal a ${credential.credential_type}: ${e.message}" }
                }
            }
        }
        return resealed
    }

    companion object : Loggable() {
        private val FINGERPRINT_TYPES = setOf(
            CredentialType.BIOMETRICS_PINDATA,
            CredentialType.BIOMETRICS_WATCHONLY_CREDENTIALS,
            CredentialType.BIOMETRICS_MNEMONIC,
        )

        fun credentialsToReseal(
            credentials: List<LoginCredentials>,
            greenKeystore: GreenKeystore
        ): List<LoginCredentials> = credentials.filter {
            it.credential_type !in FINGERPRINT_TYPES &&
                it.keystore.isNullOrBlank() &&
                it.encrypted_data?.let { data -> greenKeystore.needsResealing(data) } == true
        }
    }
}
