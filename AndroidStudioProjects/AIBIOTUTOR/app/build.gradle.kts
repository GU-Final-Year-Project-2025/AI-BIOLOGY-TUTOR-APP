plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.aibiotutor"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.aibiotutor"
        minSdk = 23
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
        buildConfig = true
        viewBinding = true
    }


}

dependencies {

    implementation(libs.appcompat)
    implementation("com.google.android.material:material:1.11.0")
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.sceneform.ux)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // pdf reader
    implementation("io.github.afreakyelf:Pdf-Viewer:2.2.0")
    implementation("com.joanzapata.pdfview:android-pdfview:1.0.4@aar")

    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")

    implementation ("com.github.mhiew:android-pdf-viewer:3.2.0-beta.3")

//    implementation ("com.tom-roush:pdfbox-android:2.0.27.0")

    implementation("com.tom-roush:pdfbox-android:2.0.27.0")


    implementation ("com.google.android.gms:play-services-mlkit-text-recognition:19.0.0")

    implementation ("com.google.android.gms:play-services-tflite-java:16.1.0")
    // Optional: include LiteRT Support Library
    implementation ("com.google.android.gms:play-services-tflite-support:16.1.0")

    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.google.code.gson:gson:2.10.1")


    implementation ("com.google.android.gms:play-services-tflite-java:16.1.0")
    implementation ("com.google.android.gms:play-services-tflite-support:16.1.0")
    


    //media players
    implementation("androidx.media3:media3-exoplayer:1.7.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.7.1")
    implementation("androidx.media3:media3-ui:1.7.1")
    implementation("androidx.media3:media3-ui-compose:1.7.1")

    // Volley for backend 
    implementation ("com.android.volley:volley:1.2.1")



}