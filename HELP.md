# Bitcoin (zeni) — Help

Short answers for the questions people ask most. Nothing here is financial advice. For anything else write to email@wanderthe.dev.

## Recovery phrase

The recovery phrase (24 words, sometimes 12) is the only backup of a wallet that counts. Write it on paper, keep it somewhere safe, and never type it into a website or send it to anyone. Anyone who has the words can take the coins, and nobody can restore a wallet without them.

## Recovery phrase not working

Check each word against the spelling and order you wrote down, one word at a time. Words are lower case. If the wallet had a passphrase, you need that as well; without it a different, empty wallet opens. Make sure you are on the right network (Bitcoin or testnet) and the right account type.

## Passphrase

A passphrase is an extra word or sentence added to the recovery phrase. A different passphrase opens a different wallet. It is not stored anywhere, so if you forget it the coins in that wallet cannot be reached.

## Receiving

Open Receive, show the address or QR code to the sender, and wait for the payment to appear in the transaction list. Use a fresh address each time when you can. An amount in the request is only a suggestion to the sender's wallet.

## Fees

Every on-chain payment pays a fee to the miners, not to this app. Fees rise when many people send at once. A higher fee confirms sooner; a lower one may wait hours or days. Choose the speed on the send screen.

## Hardware wallet

The wallet can sign with a hardware wallet over Bluetooth. If it does not connect: turn Bluetooth on, allow the Bluetooth permission for this app, wake the hardware wallet and unlock it, bring it close, and try again. A hardware wallet that was set up to turn off its radio cannot be reached over Bluetooth.

## The PIN server

Some hardware wallets, the Jade among them, have no secure chip. They keep your keys encrypted on the device instead, and part of the key to unlock them is held by a small server called a PIN server, or blind oracle. When you type your PIN on the device, this app passes a sealed message between the device and that server. The server can't read the message: it never learns your PIN, your recovery phrase or anything about your wallet. After three wrong PINs, the server and the device both delete their halves, and the device can only be restored from the recovery phrase.

**The device chooses the server, not this app.** A Jade uses the server its maker runs unless it was set up to use another one. If your device asks for a server this app doesn't know, you'll see a warning. Don't go on unless you set that server up yourself.

**What the server can see.** It sees the address your phone connects from and when you unlock, and it can tell when the same device comes back. If Tor is on in Settings, the request goes over Tor to the server's onion address instead, so your own address isn't shown.

**Using your own server.** You can run your own blind oracle (it is open source) and point the device at it, but only before the device holds a wallet. For a device that is already set up, that means a factory reset, pointing it at your server, and then restoring from your recovery phrase. **If your recovery phrase is wrong or missing, the coins are gone.** Your server then has to be running, and its key kept safe, every time you unlock. If its key is lost, the device can only be recovered from the phrase again. A firmware update keeps your choice; a factory reset puts the device back on the default server.

**Using no server at all.** On a Jade, choose **Temporary Signer** in the device's Options menu. You enter your recovery phrase, or scan a SeedQR, each time you use it. Nothing is stored on the device and no PIN server is involved. When it is switched off, it forgets everything. Wallets that only watch, and that send by passing a PSBT to another device, never use a PIN server either.

**Wallets kept on this phone.** If you protect a wallet kept on this phone with a PIN, it uses the same kind of blind server to unlock.

## Watch-only

A watch-only wallet is made from an extended public key (xpub) or a descriptor. It shows balances and builds payments but cannot sign them; the signing device does that.

## Usage data

This build collects no usage data and has no analytics.

## Servers slow or down

The wallet talks to public servers that can be busy or offline. Wait a minute, check your connection, and retry. By default the wallet uses electrum.jhoenicke.de, and fulcrum.grey.pw if that one doesn't answer. In Settings you can use your own Electrum server, or Tor if your network blocks the default servers.
