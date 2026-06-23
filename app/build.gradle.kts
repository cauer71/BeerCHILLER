plugins {
    id("com.android.application")
}

android {
    namespace = "com.bierchiller.app"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }
    val releaseKeystore = file("bierchiller-release.keystore")

    defaultConfig {
        applicationId = "com.bierchiller.app"
        minSdk = 23
        targetSdk = 36
        versionCode = 10369
        versionName = "1.3.69"
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        if (releaseKeystore.exists()) {
            create("release") {
                storeFile = releaseKeystore
                storePassword = "bierchiller-2026"
                keyAlias = "bierchiller"
                keyPassword = "bierchiller-2026"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            if (releaseKeystore.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

dependencies {
    implementation("androidx.core:core:1.17.0")
    implementation("androidx.dynamicanimation:dynamicanimation:1.1.0")
    implementation("com.google.android.play:app-update:2.1.0")
    testImplementation("junit:junit:4.13.2")
}
