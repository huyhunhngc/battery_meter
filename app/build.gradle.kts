plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("kotlin-kapt")
    id("kotlinx-serialization")
    alias(libs.plugins.dagger.hilt.android)
    id("com.google.android.gms.oss-licenses-plugin")
}

val versionMajor = 1
val versionMinor = 0
val versionPatch = 1
val versionIncrement = 8

android {
    namespace = "io.github.ifa.glancewidget"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.ifa.glancewidget"
        minSdk = 26
        targetSdk = 34
        versionCode = versionMajor * 100000 + versionMinor * 1000 + versionPatch * 10 + versionIncrement
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
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
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "META-INF/gradle/incremental.annotation.processors"
        }
    }
    bundle {
        language {
            enableSplit = false
        }
    }
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
    implementation(libs.material3)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.daggerHiltAndroid)
    implementation(libs.kotlinSerializationJson)
    implementation(libs.androidx.glance.appwidget.preview)
    implementation(libs.appwidget.host)
    implementation(libs.androidx.glance.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.permission)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.browser)
    implementation(libs.play.services.oss.licenses)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.work.multiprocess)
    implementation(libs.androidx.hilt.work)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.billing)
    implementation(libs.billing.ktx)
    implementation(libs.compose)
    implementation(libs.compose.m2)
    implementation(libs.compose.m3)
    implementation(libs.core)
    implementation(libs.views)
    kapt(libs.androidx.hilt.compiler)
    kapt(libs.daggerHiltAndroidCompiler)
    kapt(libs.daggerHiltCompiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.daggerHiltAndroidTesting)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
}