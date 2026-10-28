plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.sugarcheck.app"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.sugarcheck.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"
    }
}
