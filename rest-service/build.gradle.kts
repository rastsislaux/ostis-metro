plugins {
    kotlin("jvm") version "1.9.0"
    id("io.ktor.plugin") version "2.2.3"
    id("io.freefair.lombok") version "8.3"
    id("org.jetbrains.kotlin.plugin.lombok") version "1.8.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.artrayme:jmantic:0.7.0")
    implementation("ch.qos.logback:logback-classic:1.4.7")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.14.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("io.ktor:ktor-server-netty:2.3.4")
    implementation("io.ktor:ktor-server-cors:2.3.4")
    implementation("io.ktor:ktor-network-tls-certificates:2.3.4")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4.2")
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
    implementation("com.google.guava:guava:31.1-jre")

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("me.leepsky.MainKt")
}

ktor {
    fatJar {
        archiveFileName.set("fat.jar")
    }

    docker {
        localImageName.set("ostis-metro-middleware")
        imageTag.set("latest")
    }
}
