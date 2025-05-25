import android.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

fun decodeJwt(jwt: String): JsonObject? {
    return try {
        val payload = jwt.split(".")[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
        val decoded = String(decodedBytes)
        Json.parseToJsonElement(decoded).jsonObject
    } catch (e: Exception) {
        null
    }
}
