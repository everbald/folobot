import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.getByName<BootJar>("bootJar") { enabled = true; archiveBaseName.set(rootProject.name) }
tasks.getByName<Jar>("jar") { enabled = false }

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("plugin.jpa") version "1.8.10"
    kotlin("plugin.allopen") version "1.8.10"
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.spring") version "1.8.10"
    id("com.palantir.docker") version "0.34.0"
}

group = "com.telegram"
version = "5.0.0"
description = "folobot"

repositories {
    mavenCentral()
}

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // db
    implementation("org.postgresql:postgresql")
    runtimeOnly("org.liquibase:liquibase-core:4.20.0")

    // HTTP client engine
    implementation("io.ktor:ktor-client-apache:2.2.4")

    // telegram api
    implementation("org.telegram:telegrambots:6.7.0")

    // open api
    implementation("com.aallam.openai:openai-client:3.2.5")

    // serialisation
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    // testing
    implementation("org.junit.jupiter:junit-jupiter:5.9.2")

    // formatter
    implementation("com.ibm.icu:icu4j:72.1")

    // charts
    implementation("jfree:jfreechart:1.0.13")

    // converter
    implementation("net.bramp.ffmpeg:ffmpeg:0.7.0")
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

docker {
    name = "everbald/${rootProject.name}:latest"
    tasks.getByName<BootJar>("bootJar").let {
        this.dependsOn(it)
        this.files(it.archiveFile)
    }
    tag("GitHub", "everbald/${rootProject.name}:latest")
}