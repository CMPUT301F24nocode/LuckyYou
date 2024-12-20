plugins {
    alias(libs.plugins.android.application)
//    alias(libs.plugins.google.gms.google.services)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.projectv2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.projectv2"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.circleimageview)
    implementation (libs.cardview)
    implementation("com.google.firebase:firebase-storage:20.2.1")
    implementation(libs.glide)
    implementation(libs.activity)
    implementation(libs.espresso.intents)
    implementation(libs.fragment.testing)
    implementation(fileTree(mapOf(
        "dir" to "/Users/prabhjotsingh/Library/Android/sdk/platforms/android-34",
        "include" to listOf("*.aar", "*.jar")

    )))
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.gms:play-services-maps:18.0.2")
    implementation ("androidx.work:work-runtime-ktx:2.8.0")
    implementation (libs.zxing.android.embedded)
    implementation (libs.core)
    implementation("com.google.firebase:firebase-storage:20.2.1")
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-functions")
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.0.2")
    implementation(libs.zxing.android.embedded)
    implementation(libs.core)
    implementation(libs.swiperefreshlayout)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation ("org.mockito:mockito-core:4.11.0")
    implementation("org.mockito:mockito-android:5.14.2")
    testImplementation("org.mockito:mockito-inline:4.11.0")
    annotationProcessor(libs.glide.compiler)
    implementation(libs.lottie)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlinx.coroutines.core)
}