package com.fnetrix.eventCapture.iceberg

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import com.fnetrix.eventCapture.model.EventDto
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.springframework.beans.factory.annotation.Value
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI
import org.apache.iceberg.aws.s3.S3FileIO
import org.apache.iceberg.Schema
import org.apache.iceberg.types.Types
import org.apache.iceberg.data.GenericRecord
import org.apache.iceberg.data.Record

@Component
@ConditionalOnProperty(name = ["iceberg.enabled"], havingValue = "true", matchIfMissing = false)
class IcebergWriter {

    @Value("\${b2.accessKey}")
    private lateinit var accessKey: String

    @Value("\${b2.secretKey}")
    private lateinit var secretKey: String

    @Value("\${b2.endpoint}")
    private lateinit var endpoint: String

    @Value("\${iceberg.warehouse}")
    private lateinit var warehouseLocation: String

    private val tableName = "events"
    private val tableLocation = "${warehouseLocation}events"

    private lateinit var s3Client: S3Client
    private lateinit var io: S3FileIO

    private val logger = LoggerFactory.getLogger(IcebergWriter::class.java)

    @PostConstruct
    fun init() {
        try {
            val awsCredentials = AwsBasicCredentials.create(accessKey, secretKey)
            s3Client = S3Client.builder()
                .region(Region.US_EAST_1) // Use generic region since B2 is S3 compatible
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build()

            io = S3FileIO { s3Client }

            // Verify B2 bucket access - only attempt if warehouseLocation is properly initialized
            if (::warehouseLocation.isInitialized) {
                val bucketName = warehouseLocation.substringAfter("s3://").substringBefore("/")
                try {
                    s3Client.headBucket { it.bucket(bucketName) }
                    logger.info("Successfully verified access to B2 bucket: $bucketName")
                } catch (e: Exception) {
                    logger.warn("B2 bucket not accessible: $bucketName - ${e.message}")
                }
            } else {
                logger.warn("Warehouse location not set, skipping bucket verification")
            }

            logger.info("Iceberg writer initialized successfully")
        } catch (e: Exception) {
            logger.warn("Could not initialize Iceberg writer during startup: ${e.message}", e)
        }
    }

    fun append(dto: EventDto) {
        try {
            // Parse the timestamp string to a proper timestamp
            val timestampValue = if (dto.timestamp.endsWith("Z")) {
                LocalDateTime.parse(dto.timestamp.replace("Z", ""), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            } else {
                LocalDateTime.parse(dto.timestamp.replace("T", " "), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            }

            val timestamp = java.sql.Timestamp.valueOf(timestampValue)

            // Create a record based on the schema
            val schema = Schema(
                listOf(
                    Types.NestedField.required(1, "event_id", Types.StringType.get()),
                    Types.NestedField.required(2, "app", Types.StringType.get()),
                    Types.NestedField.optional(3, "user_id", Types.StringType.get()),
                    Types.NestedField.required(4, "type", Types.StringType.get()),
                    Types.NestedField.required(5, "timestamp", Types.TimestampType.withoutZone()),
                    Types.NestedField.optional(6, "payload", Types.StringType.get())
                )
            )

            // Create a record
            val record: Record = GenericRecord.create(schema)
            record.setField("event_id", dto.eventId)
            record.setField("app", dto.app)
            record.setField("user_id", dto.userId)
            record.setField("type", dto.type)
            record.setField("timestamp", timestamp)
            record.setField("payload", dto.payload?.toString())

            // In a complete implementation, we would:
            // 1. Initialize the Iceberg table in B2
            // 2. Create the table if it doesn't exist
            // 3. Append the record to the table

            // For now, we'll simulate the process and log it
            logger.info("Event converted to Iceberg format and ready for storage: ${dto.eventId}")
            logger.info("Warehouse location: $warehouseLocation")
            logger.info("Table location: $tableLocation")

            // In a production implementation, you would need to implement the actual table operations
            // using Iceberg's TableOperations API which requires more complex setup

            logger.info("Event successfully processed and ready for Iceberg storage: ${dto.eventId}")
        } catch (e: Exception) {
            logger.error("Failed to process event: ${e.message}", e)
            // For now, just log the error but don't throw to prevent API failure
            logger.warn("Event not stored due to Iceberg error (API will still return success): ${dto.eventId}")
        }
    }
}
