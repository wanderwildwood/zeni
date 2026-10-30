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

The wallet can sign with a Jade over Bluetooth. If it does not connect: turn Bluetooth on, allow the Bluetooth permission for this app, wake the Jade and unlock it, bring it close, and try again. A Jade that was set up to disable radio connections cannot be reached over Bluetooth.

## Watch-only

A watch-only wallet is made from an extended public key (xpub) or a descriptor. It shows balances and builds payments but cannot sign them; the signing device does that.

## Timelocks

Some older multisig accounts hand the funds back to a single key after a timelock expires. If a notice talks about re-enabling protection, follow the prompt in the account.

## Two-factor accounts

Two-factor (multisig) accounts need your key and a second signature from a server. The wallet remains non-custodial: if the service goes away you can recover after the timelock with the recovery tool linked in Settings.

## Lightning

Lightning payments need a channel opened by a service provider. Opening one can carry a funding fee, shown before you accept it.

## Liquid

Liquid is a sidechain with its own assets. Receiving an asset needs a Liquid address. Exporting the master blinding key lets another wallet see your Liquid amounts; share it only with a wallet you trust.

## Swaps

On-chain swaps need a hardware wallet that supports them and the feature turned on in its settings. If swaps are unavailable, check the connection and try again later.

## Usage data

This build collects no usage data and has no analytics.

## Servers slow or down

The wallet talks to public servers that can be busy or offline. Wait a minute, check your connection, and retry. In Settings you can use your own Electrum server, or Tor if your network blocks the default servers.
