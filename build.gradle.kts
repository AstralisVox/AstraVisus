import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("maven-publish")
    id("java-library")
    id("com.gradleup.shadow") version "9.3.2"
}

group = "me.astralisvox"
version = "1.0.1"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation(files(layout.projectDirectory.file("libs/AstraLibs-1.0.1.jar")))
    implementation("org.bstats:bstats-bukkit:3.2.1")
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.2-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(22))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.isIncremental = true
}

tasks.named<ShadowJar>("shadowJar") {
  archiveFileName.set("${project.name}-${project.version}.jar")
  relocationPrefix = "me.astralisvox.astravisus.libs"
  relocate("org.bstats", "me.astralisvox.astravisus.libs.bstats")
  relocate("me.astralisvox.astralibs", "me.astralisvox.astravisus.libs.astralibs")
}