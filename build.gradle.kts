import com.android.build.gradle.internal.scope.ProjectInfo.Companion.getBaseName

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
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
            compileSdk = 34
            defaultConfig {
                minSdk = 21
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("proguard-rules.txt")
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
            }
        }
        extensions.configure(PublishingExtension::class.java) {
            val artifactName = name
            publications {
                register<MavenPublication>("release") {
                    groupId = "com.github.wrabot.AndroidKotlinTools"
                    artifactId = artifactName
                    version = "0.16"
                    afterEvaluate {
                        from(components["release"])
                    }
                }
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
}
