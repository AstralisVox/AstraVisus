plugins {
    id("java")
    id("maven-publish")
    id("java-library")
    id("com.gradleup.shadow") version "9.3.2"
}

group = "me.astravisvox"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation("org.bstats:bstats-bukkit:3.2.1")
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.2-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(22))
}

tasks.withType<JavaCompile> {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(22)
}

tasks."named("shadowJar", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar) {
archiveFileName.set("${project.name}-${project.version}.jar")
enableRelocation = true
relocationPrefix = "me.astralisvox.astravisus.libs"
}