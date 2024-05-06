plugins {
    alias(libs.plugins.androidApplication)

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "br.com.devzone"
    compileSdk = 34

    defaultConfig {
        applicationId = "br.com.devzone"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Implementa o Firebase no projeto
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))

    implementation("com.google.firebase:firebase-auth:22.3.1")

    // Implementa o Google Analytics no projeto
    implementation("com.google.firebase:firebase-analytics")

    // Implementa o Material Design no projeto
    implementation("com.google.android.material:material")
}