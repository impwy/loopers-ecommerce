plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-kafka")
    api("org.springframework.kafka:spring-kafka")

    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:kafka")

    testFixturesImplementation("org.testcontainers:kafka")
}
