import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.getByName<BootJar>("bootJar") { enabled = true; archiveBaseName.set(rootProject.name) }
tasks.getByName<Jar>("jar") { enabled = false }
plugins {
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework:spring-core") {
        exclude("commons-logging", "commons-logging")
    }
    runtimeOnly("org.springframework.boot:spring-boot-devtools")
    // db
    implementation("org.postgresql:postgresql")
    runtimeOnly("org.liquibase:liquibase-core:4.20.0")
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.43.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.43.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.43.0")
    // HTTP client engine
    implementation("io.ktor:ktor-client-apache:2.2.4")
    // feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.1.0")
    // telegram api
    implementation("org.telegram:telegrambots-longpolling:7.2.0")
    implementation("org.telegram:telegrambots-client:7.2.0")
    // open api
    implementation("com.aallam.openai:openai-client:3.7.0")
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