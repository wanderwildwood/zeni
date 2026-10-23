# Bitcoin (zeni) — Privacy Policy

Version 0.1.2. Last reviewed 23 October 2026.

## The short version

The developer collects nothing about you and runs no server the app talks to. There is no account, no sign-up, no analytics, no advertising and no crash reporting. zeni is a fork of Blockstream's Green wallet, built without Google services and with Green's analytics switched off. To work, a Bitcoin wallet has to ask a server about its addresses: by default that is Blockstream's, and you can use your own server or Tor instead. Your keys stay on your hardware wallet, or encrypted on the phone for a wallet kept there; they are never sent anywhere.

## Who is responsible

wander wildwood, the author of this fork of Blockstream's Green wallet. Contact: email@wanderthe.dev

No personal data is transmitted to or held by the developer. The services named below are run by Blockstream, an independent controller for what it receives, under its own privacy policy.

## What the app stores, and where

In the app's private storage on the phone: each wallet's name, network and an identifier derived from its public key (a hash of the xpub); the credentials to open it — for a wallet kept on the phone, its recovery phrase encrypted with your PIN and a PIN server, or with a key in the phone's keystore if you unlock with a fingerprint; for a watch-only wallet, its xpub or descriptor; the wallet library's cache of addresses and transactions; and your settings.

The app allows Android's own backup, as Green does. Android's backup and a transfer to a new phone include the wallet list, the encrypted credentials and the settings; a cloud backup is made only if it is encrypted, and the keystore keys behind fingerprint unlock are never copied off the phone. Uninstalling the app removes everything it keeps. Your recovery phrase is the only backup of a wallet that counts: keep it yourself.

## Who else receives anything, and what

Every request below also shows the service your IP address, as any connection does, unless Tor or a proxy carries it. With Tor or a proxy on, every request below goes through it; none goes around it.

- Blockstream's Electrum servers, each time a wallet is opened: the wallet's addresses, to read its balance and history, and any transaction you sign, to broadcast it. Set your own Electrum server in Settings and these go there instead.
- Blockstream's Green multisig server, each time a wallet is opened: a login attempt, so a Green two-factor (multisig) account can be found and restored. If you add such an account, it is run with that server.
- Blockstream's price chart service (green-btc-chart.blockstream.com), when the home screen or a wallet opens: the currency you show amounts in.
- The exchange-rate source chosen in the wallet's settings, through the wallet library, for the value of amounts in your currency.
- Blockstream's PIN server (jadepin.blockstream.com), when a Jade unlocks, or a phone wallet is unlocked with its PIN. The exchange is blinded: the server never learns your PIN or your keys.
- Blockstream's Jade firmware server (jadefw.blockstream.com), when a Jade connects: a request for the list of firmware. Firmware is downloaded only if you agree.
- Your hardware wallet, over Bluetooth or USB.
- Another phone, over NFC, only while zeni's Receive screen is open on this phone or zeni is reading a tap: the payment request on that screen (an address and any amount), and nothing else. The phone must be unlocked. A tap only fills in Send; nothing is sent until you confirm and sign.

The lock screen: with Glance (com.wanderwildwood.hitome, the author's lock-screen app) installed, and only if you switch on "The bitcoin price on the lock screen", the bitcoin price is handed to Glance and only to Glance. It is fetched from the same price chart service when the lock screen is looked at and the last price is over half an hour old, and never while Tor or a proxy is on. A balance is never handed to Glance.

Help links in the app open Blockstream's help pages in your browser, and only when you press them. In About, "feed the llamas" opens a donation page (square.link) and wanderthe.dev opens the author's website, likewise.

Never contacted: Blockstream's purchase service (Meld), Boltz or any swap service, a Liquid server, an analytics service (Green's Countly analytics is switched off in this build, and its consent prompt never shows), a crash-reporting service, a support service that uploads logs, Google's push service, and any update check.

No data is sold, rented, or shared for advertising. There is no profiling, no personalization, and no automated decision-making about you.

## Permissions, and what each is for

`INTERNET` and `ACCESS_NETWORK_STATE` — for the services above.

`BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT` (and `BLUETOOTH`, `BLUETOOTH_ADMIN` on Android 11 and older) — to find and talk to a hardware wallet. The scan is declared as never used for location.

`CAMERA` — to scan a QR code, asked for when you scan. Nothing is recorded or kept.

NFC — tap to pay, as above.

`POST_NOTIFICATIONS` — the app's status notifications.

`USE_BIOMETRIC` and `USE_FINGERPRINT` — only if you choose to unlock with a fingerprint. The fingerprint itself never reaches the app.

`FOREGROUND_SERVICE`, `WAKE_LOCK` and `RECEIVE_BOOT_COMPLETED` — declared by AndroidX WorkManager for its own scheduling.

`READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE` — declared by a library. Files you export are saved through the phone's own file picker, to a place you choose.

The store also lists `com.wanderwildwood.zeni.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`, which AndroidX declares for its own internal receivers, and a Countly broadcast permission that comes with Green's analytics library; the library is switched off in this build and sends nothing.

## Legal basis (EU / EEA and UK)

Processing on the device is carried out for your own purposes and at your own direction. Where the app contacts an outside service, that request is necessary to provide the wallet you asked for (Article 6(1)(b) GDPR); the operator is an independent controller for what it then does, under its own policy.

## Your rights

Under the GDPR and UK GDPR you have rights of access, rectification, erasure, restriction, portability and objection. Under the CCPA/CPRA and comparable North American laws you have rights to know, delete, correct and opt out of sale or sharing.

The developer holds no data about you, so there is nothing to request from them, and nothing has ever been sold or shared. What the app keeps is on your phone: remove a wallet in the app, clear the app's data, or uninstall it. For what Blockstream keeps of a request, ask it. A Bitcoin transaction, once broadcast, is public on the Bitcoin network and cannot be erased by anyone.

## Children

The app is not directed at children and does not knowingly collect anything from anyone, of any age.

## Changes

If a later version changes what the app stores or which hosts it contacts, this policy is updated in the same release and the change is described in the release notes. The source is public, so any claim here can be checked against the code:
<https://github.com/wanderwildwood/zeni>. GitHub reports its licence as GPL-3.0.
