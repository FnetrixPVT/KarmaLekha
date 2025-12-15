package  com.fnetrix.eventCapture.model

data class EventDto(
    val eventId: String?,
    val app: String,
    val userId: String?,
    val type: String,
    val timestamp: String,
    val payload: Map<String, Any>?
)