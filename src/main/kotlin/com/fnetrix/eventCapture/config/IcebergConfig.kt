package com.fnetrix.eventCapture.config

import org.apache.iceberg.Schema
import org.apache.iceberg.types.Types
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class IcebergConfig {

    @Bean
    fun icebergSchema(): Schema = Schema(
        listOf(
            Types.NestedField.optional(1, "event_id", Types.StringType.get()),
            Types.NestedField.optional(2, "app", Types.StringType.get()),
            Types.NestedField.optional(3, "user_id", Types.StringType.get()),
            Types.NestedField.optional(4, "type", Types.StringType.get()),
            Types.NestedField.optional(5, "timestamp", Types.StringType.get()),
            Types.NestedField.optional(6, "payload", Types.StringType.get())
        )
    )
}

