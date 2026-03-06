plugins {
    java
    id("org.springframework.boot") version "4.0.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.geneinator"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-json")  // Jackson for RabbitMQ JSON messages
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")  // Jackson 2.x for Spring AMQP compatibility
    implementation("org.springframework.retry:spring-retry:2.0.11")

    // Database
    runtimeOnly("org.postgresql:postgresql:42.7.7")

    // Image processing
    implementation("org.im4java:im4java:1.4.0")  // ImageMagick wrapper
    implementation("com.twelvemonkeys.imageio:imageio-core:3.12.0")
    implementation("com.twelvemonkeys.imageio:imageio-jpeg:3.12.0")
    implementation("com.twelvemonkeys.imageio:imageio-webp:3.12.0")

    // EXIF metadata extraction
    implementation("com.drewnoakes:metadata-extractor:2.19.0")

    // Utilities
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
    testImplementation("org.testcontainers:postgresql:1.20.4")
    testImplementation("org.testcontainers:rabbitmq:1.20.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
