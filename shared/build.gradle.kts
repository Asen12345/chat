import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation("io.ktor:ktor-client-core:2.3.0")
            implementation("io.ktor:ktor-client-serialization:2.3.0")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
        }



        val androidMain by getting {
            dependencies {
                // Для Android
                implementation("io.ktor:ktor-client-android:2.3.0")
            }
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)

        }
    }
}

android {
    namespace = "com.ilya.myspb"
    compileSdk = 34
    defaultConfig {
        minSdk = 26
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
