import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.2.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.3.61"
    kotlin("jvm") version "1.3.70"
    kotlin("plugin.spring") version "1.3.70"
}

group = "com.github.imflog"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    jcenter()
    google()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    // CSV parsing
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    // GCP dependencies
    implementation(platform("com.google.cloud:libraries-bom:4.2.0"))
    implementation("com.google.cloud:google-cloud-datastore")
    implementation("com.google.cloud:google-cloud-pubsub")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
