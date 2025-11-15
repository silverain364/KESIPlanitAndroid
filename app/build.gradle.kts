plugins {
    id("com.android.application")
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    kotlin("kapt") // Hilt가 사용하는 annotation processor
}

android {
    namespace = "com.example.kesi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.kesi"
        minSdk = 26
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
    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)


    implementation(platform("com.google.firebase:firebase-bom:34.5.0")) //firebase 자동 버전 관리
    implementation("com.google.android.gms:play-services-base:18.9.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-messaging:25.0.1") //FCM


    implementation("com.google.firebase:firebase-database-ktx:21.0.0")//realtime database

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth") //AUTH

    // Also add the dependencies for the Credential Manager libraries and specify their versions
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")


    implementation ("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")

    // Retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    //Gson
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(kotlin("reflect"))

    //ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.4")
    //LiveModel
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.4")


    // Hilt core
    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-android-compiler:2.52")

    // (선택) ViewModel, WorkManager 등 AndroidX 확장 기능 사용 시
    implementation("androidx.hilt:hilt-navigation-fragment:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    //datastore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
}