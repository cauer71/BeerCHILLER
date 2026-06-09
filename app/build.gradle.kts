plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.helloworld"
    compileSdk = 34
    val releaseKeystore = file("bierchiller-release.keystore")

    defaultConfig {
        applicationId = "com.bierchiller.app"
        minSdk = 23
        targetSdk = 34
        versionCode = 10315
        versionName = "1.3.13"
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
    implementation("androidx.core:core:1.13.1")
}
