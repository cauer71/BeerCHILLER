import java.util.Properties

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
    val keystoreProperties = Properties().apply {
        val propertiesFile = rootProject.file("keystore.properties")
        if (propertiesFile.exists()) {
            propertiesFile.inputStream().use(::load)
        }
    }
    val releaseStorePassword = providers.environmentVariable("BEERCHILLER_STORE_PASSWORD").orNull
        ?: keystoreProperties.getProperty("storePassword")
    val releaseKeyPassword = providers.environmentVariable("BEERCHILLER_KEY_PASSWORD").orNull
        ?: keystoreProperties.getProperty("keyPassword")
    val releaseKeyAlias = providers.environmentVariable("BEERCHILLER_KEY_ALIAS").orNull
        ?: keystoreProperties.getProperty("keyAlias", "bierchiller")
    val releaseSigningAvailable = releaseKeystore.exists()
            && !releaseStorePassword.isNullOrBlank()
            && !releaseKeyPassword.isNullOrBlank()

    defaultConfig {
        applicationId = "com.bierchiller.app"
        minSdk = 23
        targetSdk = 36
        versionCode = 10379
        versionName = "1.3.79"
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        if (releaseSigningAvailable) {
            create("release") {
                storeFile = releaseKeystore
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            if (releaseSigningAvailable) {
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

    bundle {
        language {
            enableSplit = false
        }
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
