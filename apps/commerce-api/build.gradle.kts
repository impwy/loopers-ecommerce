dependencies {
    // add-ons
    implementation(project(":modules:jpa"))
    implementation(project(":modules:redis"))
    implementation(project(":modules:kafka"))
    implementation(project(":supports:jackson"))
    implementation(project(":supports:logging"))
    implementation(project(":supports:monitoring"))

    // web
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${project.properties["springDocOpenApiVersion"]}")
    implementation("org.springframework.boot:spring-boot-starter-batch-jdbc")


    //resilience4j
    implementation ("io.github.resilience4j:resilience4j-spring-boot4:2.4.0")
    implementation ("org.springframework.boot:spring-boot-starter-aspectj")

    //feign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // querydsl
    annotationProcessor ("com.querydsl:querydsl-apt:5.0.0:jakarta")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")

    // test-fixtures
    testImplementation(testFixtures(project(":modules:jpa")))
    testImplementation(testFixtures(project(":modules:redis")))
    testImplementation(testFixtures(project(":modules:kafka")))
    testImplementation("org.awaitility:awaitility:4.2.0")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.springframework.boot:spring-boot-resttestclient")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.4.1")
    testImplementation("org.projectlombok:lombok")
    testAnnotationProcessor ("org.projectlombok:lombok")
}

dependencyManagement {
    imports {
        mavenBom("io.github.resilience4j:resilience4j-bom:2.4.0")
    }
}
