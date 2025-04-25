plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdkVersion(36)

    defaultConfig {
        applicationId = "com.dingyi.visualizer"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }
    namespace = "com.dingyi.visualizer"
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.github.ChillingVan:android-openGL-canvas:v1.5.4.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
}
