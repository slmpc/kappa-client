pluginManagement {
    repositories {
        gradlePluginPortal()
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        mavenLocal()
    }

    plugins {
        id("org.jetbrains.kotlin.jvm") version "1.9.20"
        id("fabric-loom") version "1.5-SNAPSHOT"
    }
}