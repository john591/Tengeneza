// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript{
    repositories{
        google()
    }
    dependencies{
        classpath("com.google.gms:google-services:4.4.0")
        val navVersion = "2.7.3"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}

plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}