package com.fnetrix.eventCapture.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.beans.factory.annotation.Value
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI

@Configuration
class B2Config {

    @Bean
    fun b2Client(
        @Value("\${b2.endpoint}") endpoint: String,
        @Value("\${b2.accessKey}") key: String,
        @Value("\${b2.secretKey}") secret: String
    ): S3Client {
        val uri = if (!endpoint.startsWith("http")) "https://$endpoint" else endpoint
        return S3Client.builder()
            .region(Region.US_WEST_1) // dummy region, ignored by B2
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(key, secret)))
            .endpointOverride(URI.create(uri))
            .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
            .build()
    }
}
