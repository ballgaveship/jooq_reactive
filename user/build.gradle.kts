import nu.studer.gradle.jooq.JooqEdition

plugins {
    id("com.google.cloud.tools.jib") version "3.2.0"
    id("nu.studer.jooq")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.flywaydb:flyway-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-property")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.kotest.extensions:kotest-extensions-spring")
    testImplementation("io.mockk:mockk")
    testImplementation("com.ninja-squad:springmockk")

    jooqGenerator("org.jooq:jooq")
    jooqGenerator("org.jooq:jooq-meta")
    jooqGenerator("org.jooq:jooq-meta-extensions")
    jooqGenerator("org.jooq:jooq-codegen")
    jooqGenerator("org.reactivestreams:reactive-streams")
    jooqGenerator("org.postgresql:postgresql")
    jooqGenerator("javax.xml.bind:jaxb-api")
    jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")

    runtimeOnly("com.h2database:h2")
}

tasks.jar {
    enabled = true
}

tasks.forEach {
    if (it.name == "bootJar") {
        it.enabled = false
    }
}

jib {
    from {
        image = "amazoncorretto:17.0.2"
        platforms {
//            platform {
//                architecture = "arm64"
//                os = "linux"
//            }
            platform {
                architecture = "amd64"
                os = "linux"
            }
        }
    }
    to {
        image = "ballgaveship/jooq_reactive"
        tags = setOf("${project.version}", "latest")
    }
    container {
        ports = listOf("8080")
        creationTime = "USE_CURRENT_TIMESTAMP"
        jvmFlags = listOf(
            "-XX:+UseContainerSupport",
            "-XX:+UseStringDeduplication",
            "-XX:+OptimizeStringConcat"
        )
    }
}

//sourceSets {
//    main {
//        java {
//            srcDir "src/main/java"
//        }
//        kotlin {
//            srcDir "src/main/kotlin"
//        }
//    }
//}

jooq {
    version.set("3.16.7")
    edition.set(JooqEdition.OSS)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)

            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.INFO
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://db_host:db_port/db_name"
                    user = "user"
                    password = "password"
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }
                    generate.apply {
                        isGeneratedAnnotation = false
                        isRelations = true
                        isDeprecated = false
                        isRecords = true
                        isPojos = false
                        isRoutines = true
                        isImmutablePojos = true
                        isFluentSetters = false
                        isJavaTimeTypes = false
                        isDaos = true
                        isJpaAnnotations = false
                        isInterfaces = false
                        isSpringAnnotations = false
                        isValidationAnnotations = false
                    }
                    target.apply {
                        packageName = "com.ballgaveship.user"
                        directory = "build/generated-src/jooq/main"
                    }
                }
            }
        }
    }
}