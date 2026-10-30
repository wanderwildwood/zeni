package com.blockstream.data

const val BTC_POLICY_ASSET = "btc"
const val LN_BTC_POLICY_ASSET = "lnbtc"
const val LBTC_POLICY_ASSET = "lbtc" // Used in preview mostly

const val BTC_UNIT = "BTC"
const val MBTC_UNIT = "mBTC"
const val UBTC_UNIT = "\u00B5BTC"
const val BITS_UNIT = "bits"
const val SATOSHI_UNIT = "sats"

val BitcoinUnits = listOf(BTC_UNIT, MBTC_UNIT, UBTC_UNIT, BITS_UNIT, SATOSHI_UNIT)
val TestnetUnits = listOf("TEST", "mTEST", "\u00B5TEST", "bTEST", "sTEST")

object Urls {
    // Help, terms and privacy live in this fork's own repository; nothing here points at a vendor's help desk.
    private const val ZENI = "https://github.com/wanderwildwood/zeni"
    private const val HELP = "$ZENI/blob/main/HELP.md"

    const val TERMS_OF_SERVICE = "$ZENI/blob/main/TERMS.md"
    const val PRIVACY_POLICY = "$ZENI/blob/main/PRIVACY.md"
    const val HELP_MNEMONIC_NOT_WORKING = "$HELP#recovery-phrase-not-working"
    const val HELP_RECEIVE_ASSETS = "$HELP#receiving"
    const val HELP_MNEMONIC_BACKUP = "$HELP#recovery-phrase"
    const val HELP_NLOCKTIMES = "$HELP#timelocks"
    const val HELP_FEES = "$HELP#fees"
    const val HELP_FUNDING_FEES = "$HELP#lightning"
    const val HELP_RECEIVE_CAPACITY = "$HELP#lightning"
    const val HELP_GET_LIQUID = "$HELP#liquid"
    const val HELP_JADE_USB_UPGRADE = "$HELP#hardware-wallet"
    const val HELP_WHATS_COLLECTED = "$HELP#usage-data"
    const val HELP_BIP39_PASSPHRASE = "$HELP#passphrase"
    const val HELP_LIGHTNING_SHORTCUT = "$HELP#lightning"
    const val HELP_LIGHTNING_BETA = "$HELP#lightning"
    const val HELP_JADE_AIRGAPPED = "$HELP#hardware-wallet"
    const val HELP_AMP_ASSETS = "$HELP#liquid"
    const val HELP_QR_PIN_UNLOCK = "$HELP#hardware-wallet"
    const val HELP_MASTER_BLINDING_KEY = "$HELP#liquid"
    const val HELP_2FA_PROTECTION = "$HELP#two-factor-accounts"
    const val HELP_JADE_EXPORT_XPUB = "$HELP#watch-only"
    const val HELP_HW_EXPORT_XPUB = "$HELP#watch-only"
    const val HELP_CENTER = HELP
    const val RECOVERY_TOOL = "https://github.com/greenaddress/garecovery"
    const val JADE_TROUBLESHOOT = "$HELP#hardware-wallet"
    const val HELP_JADE_SWAP = "$HELP#swaps"
    const val HELP_PERFORM_SWAP = "$HELP#swaps"
    const val HELP_SWAPS_UNAVAILABLE = "$HELP#swaps"
    const val LEDGER_SUPPORTED_ASSETS = "$HELP#hardware-wallet"
    const val BLUETOOTH_PERMISSIONS = "https://developer.android.com/guide/topics/connectivity/bluetooth/permissions"
    const val SECURITY_WATCH_ONLY = "$HELP#watch-only"
    const val STATUS_PAGE = "$HELP#servers-slow-or-down"
    const val APP_PAGE = ZENI
}
