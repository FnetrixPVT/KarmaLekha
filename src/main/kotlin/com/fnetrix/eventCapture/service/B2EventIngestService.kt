package com.fnetrix.eventCapture.service

import com.fnetrix.eventCapture.model.EventDto
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Value

@Service
@ConditionalOnProperty(name = ["iceberg.enabled"], havingValue = "false", matchIfMissing = true)
class B2DirectEventIngestService(
    private val s3Client: S3Client,
    @Value("\${b2.bucket}") private val bucket: String,
    @Value("\${iceberg.table:events}") private val table: String = "events"
) : EventIngestServiceInterface {

    private val objectMapper = jacksonObjectMapper()

    override fun writeToIceberg(dto: EventDto) {
        // Serialize the event to JSON
        val eventJson = objectMapper.writeValueAsString(dto)

        // Create a unique key for the event in B2
        val eventKey = "events/${table}/${dto.eventId}_${System.currentTimeMillis()}.json"

        // Store directly to B2 using S3 API
        val request = PutObjectRequest.builder()
            .bucket(bucket)
            .key(eventKey)
            .contentLength(eventJson.length.toLong())
            .build()

        s3Client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromString(eventJson))

        println("Event stored directly to B2: $eventKey")
    }
}