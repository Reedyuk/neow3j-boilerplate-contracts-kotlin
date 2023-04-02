plugins {
    kotlin("jvm") version "1.8.0"
    id("io.neow3j.gradle-plugin") version "3.19.3"
    application
}

group = "me.andrewreed"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    create("deploy") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output

//        dependencies {
//            implementation("io.neow3j:devpack:3.19.3")
//            implementation("ch.qos.logback:logback-classic:1.2.11")
//            implementation("commons-codec:commons-codec:1.13")
//        }
    }
}

val deployImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

configurations["deployRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

dependencies {
    implementation("io.neow3j:devpack:3.19.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("io.neow3j:devpack-test:3.19.3")
    testImplementation("ch.qos.logback:logback-classic:1.3.4")

    deployImplementation("io.neow3j:compiler:3.19.2")
    deployImplementation("ch.qos.logback:logback-classic:1.2.11")
    deployImplementation("commons-codec:commons-codec:1.13")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

neow3jCompiler {
    className.set("me.andrewreed.HelloWorldSmartContract")
}