plugins {
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
	kotlin("plugin.jpa") version "1.9.22"

	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "com.fnetrix"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {

	// Spring
	implementation("org.springframework.boot:spring-boot-starter-web") {
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
	}
	implementation("org.springframework.boot:spring-boot-starter-log4j2")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// Iceberg – core Java API (NO Spark, NO Hadoop)
	implementation("org.apache.iceberg:iceberg-api:1.5.2") {
		exclude(group = "org.slf4j", module = "slf4j-reload4j")
	}
	implementation("org.apache.iceberg:iceberg-core:1.5.2") {
		exclude(group = "org.slf4j", module = "slf4j-reload4j")
	}
	implementation("org.apache.iceberg:iceberg-data:1.5.2") {
		exclude(group = "org.slf4j", module = "slf4j-reload4j")
	}
	implementation("org.apache.iceberg:iceberg-parquet:1.5.2") {
		exclude(group = "org.slf4j", module = "slf4j-reload4j")
	}

	// Iceberg AWS Catalog for S3
	implementation("org.apache.iceberg:iceberg-aws:1.5.2") {
		exclude(group = "org.slf4j", module = "slf4j-reload4j")
	}
	implementation("org.apache.iceberg:iceberg-aws-bundle:1.5.2") {
		exclude(group = "org.slf4j", module = "slf4j-reload4j")
	}

	// AWS SDK
	implementation("software.amazon.awssdk:s3:2.22.0")
	implementation("software.amazon.awssdk:auth:2.22.0")
	implementation("software.amazon.awssdk:core:2.22.0")
	implementation("software.amazon.awssdk:regions:2.22.0")

	// Optional Servlet API
	implementation("javax.servlet:javax.servlet-api:4.0.1")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
	}
}

tasks.named<JavaExec>("bootRun") {
	jvmArgs = listOf(
		"--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
		"--add-opens=java.base/java.nio=ALL-UNNAMED"
	)
}
