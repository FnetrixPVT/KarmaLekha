package  com.fnetrix.eventCapture.service

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import com.fnetrix.eventCapture.model.EventDto
import com.fnetrix.eventCapture.iceberg.IcebergWriter


interface EventIngestServiceInterface {
    fun writeToIceberg(dto: EventDto)
}

@Service
@ConditionalOnProperty(name = ["iceberg.enabled"], havingValue = "true", matchIfMissing = false)
class EventIngestService(
    private val icebergWriter: IcebergWriter
) : EventIngestServiceInterface {
    override fun writeToIceberg(dto: EventDto) {
        try {
            icebergWriter.append(dto)
        } catch (e: Exception) {
            // Log the error but don't propagate it to maintain API availability
            println("Warning: Failed to write event to Iceberg: ${e.message}")
            e.printStackTrace()
            // In a production environment, you might want to implement a fallback mechanism or queue for retry
        }
    }
}
