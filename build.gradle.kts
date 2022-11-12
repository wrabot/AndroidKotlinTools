// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.21")
    }
}

subprojects {
    repositories {
        google()
        mavenCentral()
    }

    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "maven-publish")

    plugins.withType(com.android.build.gradle.LibraryPlugin::class) {
        extensions.configure(com.android.build.gradle.LibraryExtension::class.java) {
            namespace = "com.wrabot.tools.${name.removePrefix("tools-")}"
            compileSdk = 33
            defaultConfig {
                minSdk = 16
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("proguard-rules.txt")
                aarMetadata {
                    minCompileSdk = 33
                }
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
        }
        extensions.configure(PublishingExtension::class.java) {
            publications {
                register<MavenPublication>("release") {
                    groupId = "com.github.wrabot.AndroidKotlinTools"
                    artifactId = name
                    version = "0.20"
                    afterEvaluate {
                        from(components["release"])
                    }
                }
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}
