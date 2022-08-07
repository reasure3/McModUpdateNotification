import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
//    kotlin("plugin.serialization") version "1.7.10"
    application
}

group = "com.reasure.discordbot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
    maven("https://jitpack.io/")
}

dependencies {
    testImplementation(kotlin("test"))

    // https://github.com/DV8FromTheWorld/JDA/releases/tag/v4.4.0
    val JDA_VERSION: String by project
    val JDA_KTS_VERSION: String by project

    implementation("net.dv8tion:JDA:${JDA_VERSION}")
    implementation("com.github.minndevelopment:jda-ktx:${JDA_KTS_VERSION}")

    implementation("com.google.code.gson:gson:2.9.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0-RC")

    implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.reasure.discordbot.mcupdatenoti.McUpdateNotification")
}