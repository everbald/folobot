import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.getByName<BootJar>("bootJar") { enabled = true; archiveBaseName.set(rootProject.name) }
tasks.getByName<Jar>("jar") { enabled = false }

plugins {
    id("org.springframework.boot") version "3.0.1"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("plugin.jpa") version "1.7.21"
    kotlin("plugin.allopen") version "1.7.21"
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.spring") version "1.7.21"
    id("com.palantir.docker") version "0.34.0"
}

group = "com.telegram"
version = "4.0.0"
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
    runtimeOnly("org.liquibase:liquibase-core:4.19.0")

    // HTTP client engine
    implementation("io.ktor:ktor-client-apache:2.2.4")

    // telegram api
    implementation("org.telegram:telegrambots:6.4.0")

    // open api
    implementation("com.aallam.openai:openai-client:2.1.3")

    // serialisation
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    // testing
    implementation("org.junit.jupiter:junit-jupiter:5.9.2")

    // formatter
    implementation("com.ibm.icu:icu4j:72.1")

    // charts
    implementation("jfree:jfreechart:1.0.13")
}

allOpen {
    annotations("javax.persistence.Entity", "javax.persistence.MappedSuperclass", "javax.persistence.Embeddable")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "18" //TODO 19
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