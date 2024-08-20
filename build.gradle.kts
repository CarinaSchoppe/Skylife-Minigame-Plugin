plugins {
    kotlin("jvm") version "+"
    idea
    id("com.github.johnrengelman.shadow") version "+"
    id("xyz.jpenilla.run-paper") version "+"
}

group = "com.carinaschoppe"
version = "1.0-SNAPSHOT"
description = "Skylife is a Skywars Main-System Plugin used for a remake of Smash and Skywars"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {

    //exposed ORM DAO
    implementation("org.jetbrains.exposed:exposed-core:+")
    implementation("org.jetbrains.exposed:exposed-dao:+")
    implementation("org.jetbrains.exposed:exposed-jdbc:+")
    implementation("org.xerial:sqlite-jdbc:+")


    implementation("com.google.code.gson:gson:+")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:+")
    testImplementation(kotlin("test"))
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}


tasks {
    build {
        dependsOn("shadowJar")
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release.set(21)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    test {
        useJUnitPlatform()
    }

    runServer {
        minecraftVersion("1.21.1")
    }
}

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 17 on systems that only have JDK 8 installed for example.
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}