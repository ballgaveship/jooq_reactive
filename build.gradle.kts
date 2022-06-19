import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.spring") version "1.7.0"
    java
}

val projectVersion = "2022.0.1"

val springCloudBom = "2021.0.1"
val kotlinLogging = "1.12.5"
val coroutinesTest = "1.6.2"
val kotest = "5.3.0"
val kotestExtensions = "1.1.1"
val mockk = "1.12.2"
val springmockk = "3.1.1"

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("nu.studer:gradle-jooq-plugin:7.1.1")
    }
}

allprojects {
    group = "com.gaveship"
    version = projectVersion

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")

    dependencies {
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("io.github.microutils:kotlin-logging")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    }

    dependencyManagement {
        dependencies {
            imports {
                mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudBom")
            }
            dependency("org.jooq:jooq:3.16.7")
            dependency("io.github.microutils:kotlin-logging:$kotlinLogging")
            dependency("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesTest")
            dependency("io.kotest:kotest-runner-junit5:$kotest")
            dependency("io.kotest:kotest-property:$kotest")
            dependency("io.kotest:kotest-assertions-core:$kotest")
            dependency("io.kotest.extensions:kotest-extensions-spring:$kotestExtensions")
            dependency("io.mockk:mockk:$mockk")
            dependency("com.ninja-squad:springmockk:$springmockk")
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}