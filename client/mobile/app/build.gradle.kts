plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.app"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
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
}

dependencies {
    implementation(libs.litert.support.api)
    implementation ("com.google.android.material:material:1.5.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.google.android.gms:play-services-auth:21.3.0")
    implementation ("io.github.chaosleung:pinview:1.4.4")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
}

