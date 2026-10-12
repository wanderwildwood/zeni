package com.blockstream.green.services

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import com.blockstream.compose.nfc.NfcShare

// zeni: tap-to-pay, the receiving side. While a Receive screen is in front (see NfcShare), this
// phone answers as an NFC Forum Type 4 tag whose one NDEF record is the bitcoin: payment URI.
// Any phone that reads NDEF can tap it: zeni opens Send with it, other Androids offer their
// bitcoin wallet. The tag is read-only, and with no address on screen it answers nothing.
class NfcTagService : HostApduService() {

    private enum class File { NONE, CC, NDEF }

    private var selected = File.NONE
    private var ndefFile: ByteArray? = null

    override fun processCommandApdu(apdu: ByteArray, extras: Bundle?): ByteArray {
        if (apdu.size < 4) return SW_WRONG_LENGTH
        val ins = apdu[1].toInt() and 0xFF
        val p1 = apdu[2].toInt() and 0xFF

        return when {
            ins == 0xA4 && p1 == 0x04 -> selectApplication(apdu)
            ins == 0xA4 && p1 == 0x00 -> selectFile(apdu)
            ins == 0xB0 -> readBinary(apdu)
            else -> SW_NOT_SUPPORTED
        }
    }

    private fun selectApplication(apdu: ByteArray): ByteArray {
        val aid = apdu.dataField() ?: return SW_WRONG_LENGTH
        if (!aid.contentEquals(NDEF_AID)) return SW_FILE_NOT_FOUND
        // Taken once per tap, so every read in this session sees the same request.
        val uri = NfcShare.uri.value ?: return SW_FILE_NOT_FOUND
        val message = NdefMessage(NdefRecord.createUri(uri)).toByteArray()
        ndefFile = byteArrayOf((message.size shr 8).toByte(), message.size.toByte()) + message
        selected = File.NONE
        return SW_OK
    }

    private fun selectFile(apdu: ByteArray): ByteArray {
        if (ndefFile == null) return SW_FILE_NOT_FOUND
        val id = apdu.dataField() ?: return SW_WRONG_LENGTH
        selected = when {
            id.contentEquals(CC_FILE_ID) -> File.CC
            id.contentEquals(NDEF_FILE_ID) -> File.NDEF
            else -> return SW_FILE_NOT_FOUND
        }
        return SW_OK
    }

    private fun readBinary(apdu: ByteArray): ByteArray {
        val ndef = ndefFile ?: return SW_FILE_NOT_FOUND
        val file = when (selected) {
            File.CC -> capabilityContainer(ndef.size)
            File.NDEF -> ndef
            File.NONE -> return SW_FILE_NOT_FOUND
        }
        val offset = ((apdu[2].toInt() and 0x7F) shl 8) or (apdu[3].toInt() and 0xFF)
        if (offset > file.size) return SW_WRONG_PARAMETERS
        val le = if (apdu.size > 4) (apdu[4].toInt() and 0xFF).let { if (it == 0) 256 else it } else 256
        val end = minOf(file.size, offset + le)
        return file.copyOfRange(offset, end) + SW_OK
    }

    override fun onDeactivated(reason: Int) {
        selected = File.NONE
        ndefFile = null
    }

    // Lc-prefixed data of a short APDU, or null when the lengths don't add up.
    private fun ByteArray.dataField(): ByteArray? {
        if (size < 5) return null
        val lc = this[4].toInt() and 0xFF
        if (size < 5 + lc) return null
        return copyOfRange(5, 5 + lc)
    }

    companion object {
        private val NDEF_AID = hex("D2760000850101")
        private val CC_FILE_ID = hex("E103")
        private val NDEF_FILE_ID = hex("E104")

        private val SW_OK = hex("9000")
        private val SW_WRONG_LENGTH = hex("6700")
        private val SW_WRONG_PARAMETERS = hex("6B00")
        private val SW_FILE_NOT_FOUND = hex("6A82")
        private val SW_NOT_SUPPORTED = hex("6D00")

        // Mapping version 2.0, reads of up to 59 bytes, one read-only NDEF file E104.
        private fun capabilityContainer(ndefSize: Int) = byteArrayOf(
            0x00, 0x0F, 0x20, 0x00, 0x3B, 0x00, 0x34,
            0x04, 0x06, 0xE1.toByte(), 0x04,
            (ndefSize shr 8).toByte(), ndefSize.toByte(),
            0x00, 0xFF.toByte()
        )

        private fun hex(s: String) = ByteArray(s.length / 2) { s.substring(it * 2, it * 2 + 2).toInt(16).toByte() }
    }
}
