package com.blockstream.green

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.Ndef
import com.blockstream.green.services.NfcTagService
import android.content.res.Configuration
import android.os.Bundle
import android.util.Base64
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.blockstream.compose.nfc.NfcShare
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import com.blockstream.compose.GreenApp
import com.blockstream.compose.LocalActivity
import com.blockstream.compose.events.Events
import com.blockstream.compose.models.MainViewModel
import com.blockstream.compose.navigation.NavigateDestinations
import com.blockstream.compose.theme.GreenChrome
import com.blockstream.compose.theme.GreenTheme
import com.blockstream.compose.theme.stillMotion
import com.blockstream.compose.utils.compatTestTagsAsResourceId
import com.blockstream.data.config.AppInfo
import com.blockstream.data.data.CredentialType
import com.blockstream.data.data.GreenWallet
import com.blockstream.data.database.Database
import com.blockstream.data.database.wallet.LoginCredentials
import com.blockstream.data.gdk.Gdk
import com.blockstream.data.gdk.GreenJson
import com.blockstream.data.gdk.data.Network
import com.blockstream.data.gdk.data.PinData
import com.blockstream.data.managers.BluetoothManager
import com.blockstream.data.managers.SessionManager
import com.blockstream.data.managers.SettingsManager
import com.blockstream.green.data.CountlyAndroid
import com.blockstream.green.services.TaskService
import com.blockstream.green.utils.DeepLinkHandler
import com.blockstream.utils.Loggable
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class GreenActivity : AppCompatActivity() {
    private val appInfo by inject<AppInfo>()
    private val gdk: Gdk by inject()
    private val countly: CountlyAndroid by inject()
    private val database: Database by inject()
    private val sessionManager: SessionManager by inject()
    private val settingsManager by inject<SettingsManager>()
    private val bluetoothManager by inject<BluetoothManager>()

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        FileKit.init(this)

        enableEdgeToEdge()
        stillMotion()
        setContent {
            GreenChrome()
            GreenTheme {
                CompositionLocalProvider(LocalActivity provides this) {
                    GreenApp(mainViewModel = mainViewModel, modifier = Modifier.compatTestTagsAsResourceId())
                }
            }
        }

        if (appInfo.isDevelopmentOrDebug) {
            // On development flavor, you can change settings using intent data, useful for UI tests
            intent?.let {
                if (it.hasExtra(ENABLE_TESTNET)) {
                    settingsManager.saveApplicationSettings(
                        settingsManager.getApplicationSettings().copy(
                            testnet = it.getBooleanExtra(ENABLE_TESTNET, false)
                        )
                    )
                }

                if (it.hasExtra(ENABLE_TOR)) {
                    settingsManager.saveApplicationSettings(
                        settingsManager.getApplicationSettings().copy(
                            tor = it.getBooleanExtra(ENABLE_TOR, false)
                        )
                    )
                }

                if (it.hasExtra(PROXY_URL)) {
                    val proxyUrl = it.getStringExtra(PROXY_URL)
                    settingsManager.saveApplicationSettings(
                        settingsManager.getApplicationSettings().copy(
                            proxyUrl = proxyUrl,
                            proxyEnabled = it.getBooleanExtra(PROXY_ENABLED, !proxyUrl.isNullOrBlank())
                        )
                    )
                }

                if (it.hasExtra(PERSONAL_ELECTRUM_SERVER)) {
                    val electrumServer = it.getStringExtra(PERSONAL_ELECTRUM_SERVER);
                    settingsManager.saveApplicationSettings(
                        settingsManager.getApplicationSettings().copy(
                            electrumNode = !electrumServer.isNullOrBlank(),
                            personalBitcoinElectrumServer = electrumServer,
                            personalLiquidElectrumServer = electrumServer,
                            personalTestnetElectrumServer = electrumServer,
                            personalTestnetLiquidElectrumServer = electrumServer,
                        )
                    )
                }

                if (it.hasExtra(REGISTER_NETWORK_ID) && it.hasExtra(REGISTER_NETWORK_HOSTNAME)) {
                    gdk.registerCustomNetwork(
                        it.getStringExtra(REGISTER_NETWORK_ID) ?: "", it.getStringExtra(
                            REGISTER_NETWORK_HOSTNAME
                        ) ?: ""
                    )
                }

                if (it.hasExtra(ADD_WALLET)) {
                    lifecycleScope.launch {
                        val json = Json.parseToJsonElement(
                            String(
                                Base64.decode(
                                    it.getStringExtra(ADD_WALLET)!!, Base64.DEFAULT
                                )!!
                            )
                        )

                        val walletJson = json.jsonObject["wallet"]!!.jsonObject
                        val network = walletJson["network"]!!.jsonPrimitive.content
                        val name = walletJson["name"]?.jsonPrimitive?.content

                        val wallet = GreenWallet.createWallet(
                            name = name ?: network.replaceFirstChar { n -> n.titlecase() },
                            isTestnet = Network.isTestnet(network)
                        ).also {
                            database.insertWallet(it)
                        }

                        json.jsonObject["login_credentials"]?.jsonObject?.let { loginCredentialsJson ->

                            val pinData = loginCredentialsJson["pin_data"]!!.jsonObject.let {
                                PinData(
                                    encryptedData = it["encrypted_data"]!!.jsonPrimitive.content,
                                    pinIdentifier = it["pin_identifier"]!!.jsonPrimitive.content,
                                    salt = it["salt"]!!.jsonPrimitive.content,
                                )
                            }

                            database.replaceLoginCredential(
                                LoginCredentials(
                                    wallet_id = wallet.id,
                                    credential_type = CredentialType.PIN_PINDATA,
                                    network = network,
                                    pin_data = pinData,
                                    keystore = null,
                                    encrypted_data = null,
                                    counter = 0
                                )
                            )
                        }
                    }
                }
            }
        }

        handleIntent(intent)

        // Read only while resumed and not sharing an address of our own (see nfcReader).
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                NfcShare.uri.map { it == null }.distinctUntilChanged().collect { nfcReader(it) }
            }
        }

        // Start TaskService
        try {
            // Prefer starting the service here instead of GreenApplication as Android may restart the App
            // and this leads to BackgroundServiceStartNotAllowedException be thrown
            startService(Intent(applicationContext, TaskService::class.java))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        countly.onStart(this)
    }

    // zeni: permissions granted elsewhere (Android Settings, or after a "Don't ask again")
    // were only noticed on the next launch, so look again whenever the app comes back.
    override fun onResume() {
        super.onResume()
        bluetoothManager.permissionsGranted()
    }

    override fun onPause() {
        super.onPause()
        nfcReader(false)
    }

    // zeni: tap-to-pay, the paying side. Two phones that each both read and answer NFC pick
    // their roles at random, so a tap often ended with the receiver reading the payer. While
    // this app is in front and not showing a Receive address it only reads (reader mode turns
    // its own answering off), so a tap always reads the other phone's request.
    private fun nfcReader(on: Boolean) {
        val adapter = NfcAdapter.getDefaultAdapter(this) ?: return
        runCatching {
            if (on) {
                adapter.enableReaderMode(
                    this,
                    { tag -> readPaymentTag(tag) },
                    NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                    null
                )
            } else {
                adapter.disableReaderMode(this)
            }
        }
    }

    // Another zeni first (its own application, which a Kompakt's chip passes to apps), then any
    // NDEF tag. Runs on the NFC thread.
    private fun readPaymentTag(tag: Tag) {
        val uri = readZeni(tag) ?: readNdef(tag) ?: return
        runOnUiThread { sessionManager.pendingUri.value = uri }
    }

    private fun readZeni(tag: Tag): String? = runCatching {
        IsoDep.get(tag)?.use { isoDep ->
            isoDep.connect()
            val aid = NfcTagService.ZENI_AID
            val select = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, aid.size.toByte()) + aid + byteArrayOf(0x00)
            val answer = isoDep.transceive(select)
            val ok = answer.size >= 2 && answer[answer.size - 2] == 0x90.toByte() && answer[answer.size - 1] == 0x00.toByte()
            if (!ok) return@use null
            String(answer, 0, answer.size - 2, Charsets.UTF_8)
                .takeIf { it.startsWith("bitcoin:", ignoreCase = true) }
        }
    }.getOrNull()

    private fun readNdef(tag: Tag): String? = runCatching {
        Ndef.get(tag)?.use { ndef ->
            ndef.connect()
            ndef.ndefMessage?.let { bitcoinUri(listOf(it)) }
        }
    }.getOrNull()

    // The first bitcoin: URI record of a tapped tag. Only a payment request is taken; the Send
    // screen it opens still waits for the payer to confirm.
    private fun nfcPaymentUri(intent: Intent): String? {
        intent.data?.takeIf { it.scheme.equals("bitcoin", ignoreCase = true) }?.let { return it.toString() }
        @Suppress("DEPRECATION")
        val messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) ?: return null
        return bitcoinUri(messages.filterIsInstance<NdefMessage>())
    }

    private fun bitcoinUri(messages: List<NdefMessage>): String? = messages
        .flatMap { it.records.toList() }
        .firstNotNullOfOrNull { record -> record.toUri()?.takeIf { it.scheme.equals("bitcoin", ignoreCase = true) } }
        ?.toString()

    override fun onStop() {
        super.onStop()
        countly.onStop()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        countly.onConfigurationChanged(newConfig)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        logger.d { "onNewIntent called with: ${intent.data}" }
        setIntent(intent) // Important: Update the activity's intent
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        logger.d { "handleIntent called with action: ${intent?.action}, data: ${intent?.data}" }

        // Handle blockstream:// scheme with DeepLinkHandler
        if (intent?.action == Intent.ACTION_VIEW && intent.data?.scheme == "blockstream") {
            logger.d { "Handling blockstream:// deep link" }
            if (DeepLinkHandler.handleDeepLink(intent.data, mainViewModel)) {
                return
            }
        }

        if (intent?.action == Intent.ACTION_VIEW && intent.data?.toString()
                ?.let { it.contains("/jade/setup") || it.contains("/j/s") } == true
        ) {
            mainViewModel.postEvent(Events.NavigateTo(NavigateDestinations.DeviceList(isJade = true)))
        } else if (intent?.action == OPEN_WALLET) {

            intent.getStringExtra(WALLET)?.let { GreenJson.json.decodeFromString<GreenWallet>(it) }?.let { wallet ->
                mainViewModel.navigate(
                    wallet = wallet, deviceId = intent.getStringExtra(DEVICE_ID)
                )
            }
        } else if (intent?.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            nfcPaymentUri(intent)?.let { sessionManager.pendingUri.value = it }
        } else if (intent?.action == HIDE_AMOUNTS) {
            settingsManager.saveApplicationSettings(
                settingsManager.getApplicationSettings().copy(hideAmounts = true)
            )
        } else {
            // Handle Uri (BIP-21 or lightning)
            intent?.data?.let {
                sessionManager.pendingUri.value = it.toString()
            }
        }
    }

    companion object : Loggable() {
        const val OPEN_WALLET = "OPEN_WALLET"
        const val WALLET = "WALLET"
        const val DEVICE_ID = "DEVICE_ID"

        const val ENABLE_TESTNET = "ENABLE_TESTNET"
        const val ENABLE_TOR = "ENABLE_TOR"
        const val PROXY_URL = "PROXY_URL"
        const val PROXY_ENABLED = "PROXY_ENABLED"
        const val PERSONAL_ELECTRUM_SERVER = "PERSONAL_ELECTRUM_SERVER"

        const val REGISTER_NETWORK_ID = "REGISTER_NETWORK_ID"
        const val REGISTER_NETWORK_HOSTNAME = "REGISTER_NETWORK_HOSTNAME"

        const val ADD_WALLET = "ADD_WALLET"
        const val HIDE_AMOUNTS = "HIDE_AMOUNTS"
    }
}