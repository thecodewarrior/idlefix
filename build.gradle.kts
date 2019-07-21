import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.40"
    id("fabric-loom") version "0.2.4-SNAPSHOT"
}

val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_version: String by project

val mod_version: String by project
val maven_group: String by project
val modid: String by project

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

base {
    archivesBaseName = modid
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "http://server.bbkr.space:8081/artifactory/libs-snapshot")
}

group = maven_group
version = mod_version

minecraft {
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("net.fabricmc:yarn:${yarn_mappings}")
    modCompile("net.fabricmc:fabric-loader:${loader_version}")
    modCompile("net.fabricmc.fabric-api:fabric-api:${fabric_version}")

    implementation(kotlin("stdlib-jdk8"))

    modCompile("io.github.prospector.modmenu:ModMenu:1.6.2-92")
    modCompile("net.fabricmc:fabric-language-kotlin:1.3.40+build.1")
    modCompile("io.github.cottonmc:cotton-client-commands:0.4.2+1.14.3-SNAPSHOT")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xjvm-default=enable"
}

sourceSets["main"].allSource.apply {
    srcDir("src/main/kotlin")
}

tasks.getByName<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(mutableMapOf(
            "version" to mod_version
        ))
    }
}
