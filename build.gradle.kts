import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.getByName<BootJar>("bootJar") { enabled = true; archiveBaseName.set(rootProject.name) }
tasks.getByName<Jar>("jar") { enabled = false }
plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.jpa") version "1.9.21"
    kotlin("plugin.noarg") version "1.9.21"
    kotlin("plugin.allopen") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
    kotlin("plugin.spring") version "1.9.21"
}

group = "com.everbald"
version = "5.0.0"
description = "folobot"

repositories {
    mavenCentral()
}

buildscript {
    extra["servicePackage"] = "com.everbald.folobot.service"
}

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-mustache:3.0.7")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.4")
    implementation("org.springframework.boot:spring-boot-starter-security:3.0.4")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.0.4")
    implementation("org.springframework:spring-core:6.0.6") {
        exclude("commons-logging", "commons-logging")
    }
    runtimeOnly("org.springframework.boot:spring-boot-devtools:3.0.4")
    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect")
    // db
    runtimeOnly("org.postgresql:postgresql:42.5.4")
    runtimeOnly("org.liquibase:liquibase-core:4.20.0")
//    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.43.0")
    // HTTP client engine
    implementation("io.ktor:ktor-client-apache:2.2.4")
    // feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.0.4")
    // telegram api
    implementation("org.telegram:telegrambots:6.8.0")
    // open api
    implementation("com.aallam.openai:openai-client:3.6.1")
    // serialisation
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    // logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    // testing
    implementation("org.junit.jupiter:junit-jupiter:5.9.2")
    // formatter
    implementation("com.ibm.icu:icu4j:72.1")
    // charts
    implementation("jfree:jfreechart:1.0.13")
    // NLP
    implementation("com.textrazor:textrazor:1.0.12")
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "19"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("stage") {
    dependsOn("build")
}