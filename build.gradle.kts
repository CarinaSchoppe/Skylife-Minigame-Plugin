plugins {
    kotlin("jvm") version "2.2.10"
    idea
    id("com.gradleup.shadow") version "9.2.2"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    jacoco
}

group = "com.carinaschoppe"
version = "1.1"
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
    implementation("org.jetbrains.exposed:exposed-core:1.0.0")
    implementation("org.jetbrains.exposed:exposed-dao:1.0.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.0.0")
    implementation("org.xerial:sqlite-jdbc:3.51.1.0")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.84.1")
    implementation("com.google.code.gson:gson:2.13.2")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.0")
    testImplementation("net.kyori:adventure-text-minimessage:4.17.0")
    testImplementation("net.kyori:adventure-text-serializer-plain:4.17.0")
    testImplementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
    testImplementation("org.mockito:mockito-core:5.21.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.1.0")
    testImplementation("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
}

val integrationTest by sourceSets.creating {
    kotlin.srcDir("src/integrationTest/kotlin")
    resources.srcDir("src/integrationTest/resources")
    compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
    runtimeClasspath += output + compileClasspath
}

configurations[integrationTest.implementationConfigurationName].extendsFrom(configurations["testImplementation"])
configurations[integrationTest.runtimeOnlyConfigurationName].extendsFrom(configurations["testRuntimeOnly"])

val integrationTestSourceSet = integrationTest

tasks {
    build {
        dependsOn("shadowJar")
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
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

    val integrationTest by registering(Test::class) {
        description = "Runs integration tests."
        group = "verification"
        testClassesDirs = integrationTestSourceSet.output.classesDirs
        classpath = integrationTestSourceSet.runtimeClasspath
        useJUnitPlatform()
        shouldRunAfter(test)
        extensions.configure(JacocoTaskExtension::class) {
            destinationFile = layout.buildDirectory.file("jacoco/integrationTest.exec").get().asFile
        }
    }

    check {
        dependsOn(integrationTest, jacocoTestReport)
    }

    jacocoTestReport {
        dependsOn(test, integrationTest)
        executionData.setFrom(
            layout.buildDirectory.asFileTree.matching {
                include("jacoco/test.exec", "jacoco/integrationTest.exec")
            }
        )
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    runServer {
        minecraftVersion("1.21.8")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    jvmToolchain(21)
}
