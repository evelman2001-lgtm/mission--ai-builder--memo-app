import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.0.21"
    id("org.jetbrains.compose") version "1.7.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
}

group = "com.macnotes"
version = "1.0.0"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions { jvmTarget = "21" }
}
tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "21"
    targetCompatibility = "21"
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.commonmark:commonmark:0.23.0")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.23.0")
    implementation("org.commonmark:commonmark-ext-gfm-strikethrough:0.23.0")
    implementation("org.commonmark:commonmark-ext-task-list-items:0.23.0")
}

compose.desktop {
    application {
        mainClass = "com.macnotes.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            packageName = "MacNotes"
            packageVersion = "1.0.0"
            description = "Personal Markdown Notepad for Mac"
            macOS {
                bundleID = "com.macnotes.app"
            }
        }
    }
}
