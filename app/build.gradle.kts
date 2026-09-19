plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.hakamo.ivcalculator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hakamo.ivcalculator"
        minSdk = 23
        targetSdk = 35
        versionCode = 10
        versionName = "6.2"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
}
