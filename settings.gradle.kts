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
        id("org.jetbrains.kotlin.jvm") version "2.0.0-Beta4"
        id("fabric-loom") version "1.5-SNAPSHOT"
    }
}