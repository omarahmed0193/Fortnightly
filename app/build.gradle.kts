plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("androidx.navigation.safeargs")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "com.afterapps.fortnightly"
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            val apiKey: String = project.property("NEWS_API_KEY") as String
            buildConfigField("String", "NEWS_API_KEY", apiKey)
        }
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    kapt {
        javacOptions {
            // These options are normally set automatically via the Hilt Gradle plugin, but we
            // set them manually to workaround a bug in the Kotlin 1.5.20
            option("-Adagger.fastInit=ENABLED")
            option("-Adagger.hilt.android.internal.disableAndroidSuperclassValidation=true")
        }
    }
}

dependencies {

    //Android Support Libraries
    implementation(Kotlin.stdlib)
    implementation(AndroidX.Core.ktx)
    implementation(AndroidX.appCompat)
    implementation(Google.Android.material)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.fragmentKtx)
    implementation(AndroidX.recyclerView)
    implementation(AndroidX.swipeRefreshLayout)

    //LiveData
    implementation(AndroidX.Lifecycle.liveDataKtx)
    implementation(AndroidX.Lifecycle.runtimeKtx)

    //ViewModel
    implementation(AndroidX.Lifecycle.viewModelKtx)
    implementation(AndroidX.Lifecycle.viewModelSavedState)

    //Room(Database)
    implementation(AndroidX.Room.runtime)
    implementation(AndroidX.Room.ktx)
    kapt(AndroidX.Room.compiler)

    //Hilt(Dependency Injection)
    implementation(Google.Dagger.Hilt.android)
    implementation(AndroidX.Hilt.work)
    kapt(AndroidX.Hilt.compiler)
    kapt(Google.Dagger.Hilt.compiler)

    //Navigation
    implementation(AndroidX.Navigation.fragmentKtx)
    implementation(AndroidX.Navigation.uiKtx)

    //Network
    implementation(Square.Retrofit2.retrofit)
    implementation(Square.OkHttp3.loggingInterceptor)
    implementation(Square.Retrofit2.Converter.moshi)
    implementation(Libs.retrofit2_kotlin_coroutines_adapter)

    //Moshi
    implementation(Square.Moshi.kotlinReflect)
    kapt(Square.Moshi.kotlinCodegen)

    //Coroutiens
    implementation(KotlinX.Coroutines.core)
    implementation(KotlinX.Coroutines.android)

    //Coil(Image Loader)
    implementation(COIL)

    //Work Manager
    implementation(AndroidX.Work.runtimeKtx)

    //Unit Test Libraries
    testImplementation(Libs.mockk)
    testImplementation(Libs.truth)
    testImplementation(Libs.turbine)
    testImplementation(Libs.kotlinx_coroutines_test) {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-debug")
    }
    testImplementation(Libs.junit_junit)
    testImplementation(AndroidX.ArchCore.testing)

    //Android Test Libraries
    androidTestImplementation(Libs.truth)
    androidTestImplementation(Libs.kotlinx_coroutines_test) {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-debug")
    }
    androidTestImplementation(Libs.mockk_android)
    androidTestImplementation(AndroidX.ArchCore.testing)
    androidTestImplementation(Libs.turbine)
    androidTestImplementation(AndroidX.Test.Ext.junit)
    androidTestImplementation(AndroidX.Test.Espresso.core)
}