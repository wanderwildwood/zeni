package com.blockstream.data.data

import com.blockstream.data.gdk.GreenJson
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64

@Serializable
data class EncryptedData(private val encryptedData: String, private val iv: String) :
    GreenJson<EncryptedData>() {
    fun getEncryptedData(): ByteArray = Base64.decode(encryptedData)
    fun getIv(): ByteArray = Base64.decode(iv)
    override fun kSerializer() = serializer()

    /**
     * Data sealed with AES-GCM carries a 12-byte nonce; data written before 0.1.3 with AES-CBC
     * carries a 16-byte IV. The length is how the two are told apart.
     */
    fun isGcm(): Boolean = getIv().size == GCM_NONCE_BYTES

    companion object {
        const val GCM_NONCE_BYTES = 12

        fun fromByteArray(encryptedData: ByteArray, iv: ByteArray) = EncryptedData(
            Base64.encode(encryptedData),
            Base64.encode(iv)
        )

        fun fromString(jsonString: String): EncryptedData? {
            return try {
                json.decodeFromString(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}