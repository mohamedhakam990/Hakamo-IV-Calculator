plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.hakamo.ivcalculator"
    compileSdk = 35
    defaultConfig { applicationId = "com.hakamo.ivcalculator"; minSdk = 23; targetSdk = 35; versionCode = 8; versionName = "8.0" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}
kotlin { jvmToolchain(17) }
dependencies { implementation("androidx.appcompat:appcompat:1.7.0") }
