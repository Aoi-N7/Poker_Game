plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Pythonの導入により追加
    id("com.chaquo.python")

}

android {
    namespace = "com.example.poker_game"
    compileSdk = 34

    // Pytnonにより追加
    flavorDimensions += "pyVersion"
    productFlavors{
        create("py312"){dimension = "pyVersion"}
    }

    defaultConfig {

        // Pythonにより追加
        ndk{
            abiFilters += listOf("arm64-v8a", "x86_64")
        }

        applicationId = "com.example.poker_game"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

// Pythonにより追加
chaquopy{
    defaultConfig{
        buildPython("C:/Python/Python3/python.exe")
        version = "3.12"

        /*
        pip{
            install("random")
        }
         */

    }
    productFlavors{
        getByName("py312"){version = "3.12"}
    }
    sourceSets{ }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //implementation("'com.android.tools.build:gradle:7.0.0'")
    //implementation ("com.chaquo.python:gradle:15.0.1")
}