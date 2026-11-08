# Bitcoin (zeni) — Privacy Policy

Version 0.1.8. Last reviewed 7 November 2026.

## The short version

This app is an independent fork of Blockstream's Green wallet. It is not made, endorsed or supported by Blockstream. The developer collects nothing about you and runs no server the app talks to. There is no account, no sign-up, no analytics, no advertising and no crash reporting. zeni is a fork of Blockstream's Green wallet, built without Google services and with Green's analytics switched off. To work, a Bitcoin wallet has to ask a server about its addresses: by default that is Blockstream's, and you can use your own server or Tor instead. Your keys stay on your hardware wallet, or encrypted on the phone for a wallet kept there; they are never sent anywhere. The author's address, email@wanderthe.dev, is the only support there is; Blockstream does not support this app.

## What the app stores, and where

In the app's private storage on the phone: each wallet's name, network and an identifier derived from its public key (a hash of the xpub); the credentials to open it — for a wallet kept on the phone, its recovery phrase encrypted with your PIN and a PIN server, or sealed with AES-256-GCM under a key in the phone's keystore if you unlock with a fingerprint; for a watch-only wallet, its xpub or descriptor; the wallet library's cache of addresses and transactions; and your settings.

Everything the app seals in the keystore is written with AES-256-GCM, which refuses data that has been changed. Versions before 0.1.3 used AES-CBC, as Blockstream Green does. So that a wallet set up with one of those versions still opens, the app can still read CBC data: it reads it once, seals it again with GCM, and from then on uses the GCM copy. Nothing is ever written with CBC. The CBC code stays only for that one-time move and runs inside the phone's keystore, where nothing outside the app can send it data to try.

The app does not allow Android's backup. Nothing it keeps (wallet list, encrypted credentials, settings) goes to a cloud backup or is copied to a new phone by Android's transfer; on a new phone you restore a wallet from its recovery phrase. Uninstalling the app removes everything it keeps. Your recovery phrase is the only backup of a wallet that counts: keep it yourself.

## Who else receives anything, and what

The services named below are run by Blockstream, an independent controller for what it receives, under its own privacy policy (on blockstream.com).

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

Help links in the app open Blockstream's help pages in your browser, and only when you press them. They explain the wallet technology; they are Blockstream's pages, not this app's. In Settings and in About, "feed the llamas" opens a donation page (square.link) and wanderthe.dev opens the author's website, likewise; in About, the source address and Blockstream Green's address open those projects' pages on GitHub. The sentence "Write to email@wanderthe.dev" in About, under How to contact us, opens your email app with a new message to that address, its subject the app's name and version; nothing is sent until you send it, and no logs or wallet data are attached.

Never contacted: Blockstream's purchase service (Meld), Boltz or any swap service, a Liquid server, an analytics service (Green's Countly library and the Zendesk help-desk library are removed from this build), a crash-reporting service, a support service that uploads logs, Google's push service, and any update check.

A Bitcoin transaction, once broadcast, is public on the Bitcoin network and cannot be erased by anyone.

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

The store also lists `com.wanderwildwood.zeni.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`, which AndroidX declares for its own internal receivers. The Countly analytics library that once added a broadcast permission has been removed.

## Legal basis (EU / EEA and UK)

Processing on the device is carried out for your own purposes and at your own direction. Where the app contacts an outside service, that request is necessary to provide the wallet you asked for (Article 6(1)(b) GDPR); the operator is an independent controller for what it then does, under its own policy.

## Children

The app is not directed at children and does not knowingly collect anything from anyone, of any age.

<!-- shared-about privacy section -->
## Who we are

Bitcoin (zeni) is made by wander wildwood, an independent developer: one person, not a company. wander wildwood is the data controller for anything the app itself handles, and that is very little. What you put in Bitcoin (zeni) stays on your phone. The only things that go out are the requests described above, to Blockstream's servers and to the exchange-rate source you choose: the wallet's addresses and any transaction you sign, a login attempt at the multisig server, the currency for the price chart, a request for exchange rates, the blinded PIN exchange and a request for the firmware list. The developer receives nothing, so the developer holds no data about you.

## How to contact us

Write to email@wanderthe.dev about your data, this policy or anything else. The same address is in the app, under About.

## Your rights

If you are in the EU, the EEA or the UK, the GDPR gives you the right to:

- see the personal data held about you (access)
- have it corrected (rectification) or deleted (erasure)
- limit how it is used (restriction)
- object to its use (objection)
- get a copy to take elsewhere (portability)
- withdraw your consent at any time, where something rests on it
- complain to a data protection supervisory authority, for example the one where you live

Similar laws elsewhere, such as the CCPA in California, give you the right to know, delete and correct, and to opt out of the sale or sharing of your data. Nothing from Bitcoin (zeni) is sold or shared.

Since Bitcoin (zeni) keeps what it stores only on your phone, you can exercise most of these rights yourself: remove a wallet in the app, clear the app's data in Android's settings, or uninstall the app, which deletes all of it. For anything you cannot do yourself, write to the address above.

Bitcoin (zeni) connects to Blockstream's servers and the exchange-rate source you choose. They are separate from this app and from its developer, and what they receive is covered by their own privacy policies; ask them about what they hold.
If you point Bitcoin (zeni) at a server of your own, what that server keeps is yours to manage.
<!-- /shared-about privacy section -->

## Changes

If a later version changes what the app stores or which hosts it contacts, this policy is updated in the same release and the change is described in the release notes. The source is public, so any claim here can be checked against the code:
<https://github.com/wanderwildwood/zeni>. GitHub reports its license as GPL-3.0.
