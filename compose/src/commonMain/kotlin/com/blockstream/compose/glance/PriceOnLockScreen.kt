package com.blockstream.compose.glance

// zeni: the bitcoin price on Glance's lock-screen panel, off until switched on in Settings.
// Only the market price ever leaves the app; never a balance or anything about a wallet.

expect fun lockScreenPriceOn(): Boolean

expect fun setLockScreenPriceOn(on: Boolean)

// The currency the wallet page shows prices in, so the lock screen uses the same one.
expect fun rememberLockScreenCurrency(currency: String)
