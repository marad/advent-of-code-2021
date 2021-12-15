plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }
}

dependencies {
    implementation("io.arrow-kt:arrow-core:1.0.1")
    implementation("io.kotest:kotest-assertions-core:5.0.1")
    implementation("org.jgrapht:jgrapht-core:1.5.1")
}