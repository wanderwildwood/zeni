package com.blockstream.data.usecases

import com.blockstream.data.crypto.GreenKeystore
import com.blockstream.data.data.CredentialType
import com.blockstream.data.database.Database
import com.blockstream.data.database.wallet.LoginCredentials
import com.blockstream.data.extensions.passwordPinData
import com.blockstream.data.extensions.pinPinData
import com.blockstream.utils.Loggable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * When a fingerprint has been added to the phone since the last look, fingerprint unlock is
 * turned off for every wallet that can also be opened with its PIN (or password). Those
 * wallets ask for the PIN next time, and fingerprint unlock can be turned on again under
 * Security.
 *
 * The fingerprint key itself is never touched: it is one key shared by every wallet, and a
 * wallet set up with a fingerprint and no PIN has nothing else to open it with. Such a wallet
 * keeps its fingerprint unlock whatever happens here.
 *
 * Returns the ids of the wallets whose fingerprint unlock was turned off.
 */
class DropFingerprintAfterNewEnrollmentUseCase(
    private val database: Database,
    private val greenKeystore: GreenKeystore,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(): Set<String> = mutex.withLock {
        if (!greenKeystore.biometricEnrollmentChanged()) return emptySet()

        val dropped = mutableSetOf<String>()
        for (wallet in database.getAllWallets()) {
            val toDrop = fingerprintCredentialsToDrop(database.getLoginCredentials(wallet.id))
            for (credential in toDrop) {
                database.deleteLoginCredentials(credential)
            }
            if (toDrop.isNotEmpty()) dropped += wallet.id
        }
        // Only once every wallet is done: a failure above leaves the change to be seen again.
        greenKeystore.acknowledgeBiometricEnrollment()
        logger.i { "A fingerprint was added: fingerprint unlock turned off for ${dropped.size} wallets with a PIN" }
        dropped
    }

    companion object : Loggable() {
        val FINGERPRINT_TYPES = setOf(
            CredentialType.BIOMETRICS_PINDATA,
            CredentialType.BIOMETRICS_MNEMONIC,
            CredentialType.BIOMETRICS_WATCHONLY_CREDENTIALS,
        )

        /**
         * One wallet's fingerprint credentials, if it has a PIN or password that still opens it;
         * none at all otherwise. A PIN that has used up its tries does not count.
         */
        fun fingerprintCredentialsToDrop(credentials: List<LoginCredentials>): List<LoginCredentials> {
            val hasPin = credentials.pinPinData != null || credentials.passwordPinData != null
            if (!hasPin) return emptyList()
            return credentials.filter { it.credential_type in FINGERPRINT_TYPES }
        }
    }
}
