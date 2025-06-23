import android.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

fun decodeJwt(jwt: String): JsonObject? {
    return try {
        val parts = jwt.split(".")
        if (parts.size != 3) return null // pastikan ada 3 bagian: header.payload.signature
        val payload = parts[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val decodedString = String(decodedBytes)
        Json.parseToJsonElement(decodedString).jsonObject
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
