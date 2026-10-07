plugins {
    java
    id("org.springframework.boot") version "4.0.9-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "asgomez71"
version = "0.0.1-SNAPSHOT"
description = "aws_project01"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://repo.spring.io/snapshot")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Copy>("unpack") {
    dependsOn(tasks.named("bootJar"))

    from(
        zipTree(
            tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar")
                .get()
                .archiveFile
        )
    )

    into(layout.buildDirectory.dir("dependency"))
}

tasks.register<Exec>("docker") {
    group = "docker"
    description = "Builds the Docker image for aws_project01"

    dependsOn("unpack")

    commandLine(
        "docker",
        "build",
        "-t",
        "${project.group}/aws_project01:3.0.0",
        "."
    )
}

tasks.register<Exec>("dockerPush1.0.0") {
    group = "docker"
    description = "Builds and pushes aws_project01:1.0.0 to Docker Hub"

    dependsOn("unpack")

    commandLine(
        "docker",
        "build",
        "-t",
        "${project.group}/aws_project01:1.0.0",
        "."
    )

    doLast {
        exec {
            commandLine(
                "docker",
                "push",
                "${project.group}/aws_project01:1.0.0"
            )
        }
    }
}