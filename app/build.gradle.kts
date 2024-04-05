plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.incomeexpensemanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.incomeexpensemanager"
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
        debug {
            isDebuggable = true
            isShrinkResources = false
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
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    //retrofit2
    implementation(libs.retrofit)
    implementation(libs.retrofit.convertor)
    implementation(libs.retrofit.convertor.sacalar)
    //hilt
    implementation(libs.hilt)
    //firebase
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.activity)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    kapt(libs.hilt.compailer)
    //coroutine
    implementation(libs.kotlin.coroutines)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)
    //life-cycler
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.extensions)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    //room
    implementation(libs.room.ktx)
    implementation(libs.room)
    kapt(libs.room.compiler)


    //dataStore
    implementation(libs.datastore.preferences)

    //timber
    implementation(libs.timber)
    //customCrashActivity
    implementation(libs.custom.activity.crash)
    //sdp
    implementation(libs.sdp)
    //ssp
    implementation(libs.ssp)

    //lottie Animation
    implementation(libs.lottie.animation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //glide
    implementation(libs.glide)


    implementation("de.hdodenhof:circleimageview:3.1.0")
}