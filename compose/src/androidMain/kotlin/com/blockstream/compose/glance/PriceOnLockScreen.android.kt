package com.blockstream.compose.glance

import android.content.Context
import android.net.Uri
import org.koin.mp.KoinPlatform

actual fun lockScreenPriceOn(): Boolean = context()?.let { LockScreenPrice.on(it) } ?: false

actual fun setLockScreenPriceOn(on: Boolean) {
    context()?.let { LockScreenPrice.set(it, on) }
}

actual fun rememberLockScreenCurrency(currency: String) {
    context()?.let { LockScreenPrice.setCurrency(it, currency) }
}

private fun context(): Context? = runCatching { KoinPlatform.getKoin().get<Context>() }.getOrNull()

/** The switch and currency for the lock-screen price, read by the app's Glance provider. */
object LockScreenPrice {
    private const val PREFS = "lock_screen"
    private const val ON = "price_on"
    private const val CURRENCY = "price_currency"

    /** The provider's cached price; cleared whenever what it should show changes. */
    const val CACHE = "lock_screen_price"

    fun on(context: Context): Boolean = prefs(context).getBoolean(ON, false)

    fun set(context: Context, on: Boolean) {
        prefs(context).edit().putBoolean(ON, on).apply()
        forget(context)
    }

    fun currency(context: Context): String = prefs(context).getString(CURRENCY, null) ?: "usd"

    fun setCurrency(context: Context, currency: String) {
        val lower = currency.lowercase()
        if (lower == currency(context)) return
        prefs(context).edit().putString(CURRENCY, lower).apply()
        forget(context)
    }

    private fun forget(context: Context) {
        context.getSharedPreferences(CACHE, Context.MODE_PRIVATE).edit().clear().apply()
        context.contentResolver.notifyChange(Uri.parse("content://${context.packageName}.glance/lines"), null)
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
