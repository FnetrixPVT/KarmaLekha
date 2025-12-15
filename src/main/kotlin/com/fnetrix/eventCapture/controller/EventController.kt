package  com.fnetrix.eventCapture.controller

import com.fnetrix.eventCapture.model.EventDto
import com.fnetrix.eventCapture.service.EventIngestServiceInterface
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/events")
class EventController(
    private val ingestService: EventIngestServiceInterface
) {
    @PostMapping
    fun ingest(@RequestBody dto: EventDto): ResponseEntity<String> {
        ingestService.writeToIceberg(dto)
        return ResponseEntity.ok("event stored")
    }
}