import org.gradle.kotlin.dsl.implementation

plugins {
    `java-library`
    `java-test-fixtures`
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-redis")
    implementation ("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    testFixturesImplementation("com.redis:testcontainers-redis")
}
