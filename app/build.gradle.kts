import com.apollographql.apollo3.gradle.internal.ApolloDownloadSchemaTask
import java.util.Properties

apply(from = "keystore.gradle")

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.apollographql.apollo3") version "3.4.0"
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use {
        localProperties.load(it)
    }
}

val api_endpoint = localProperties.getProperty("api_endpoint") ?: "default_api_endpoint"
val api_key = localProperties.getProperty("api_key") ?: "default_api_key"

android {
    namespace = "com.starpx"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.starpx"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "API_ENDPOINT", "\"${api_endpoint}\"")
        buildConfigField("String", "API_KEY", "\"${api_key}\"")
    }

    signingConfigs {
        create("release") {
            keyAlias = findProperty("keyAlias") as String
            keyPassword = findProperty("keyPassword") as String
            storeFile = file(findProperty("storeFile") as String)
            storePassword = findProperty("storePassword") as String
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
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
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

apollo {
    service("service") {
        packageNamesFromFilePaths()
        generateKotlinModels.set(true)
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.aws.android.sdk.core)
    implementation(libs.aws.android.sdk.cognitoidentityprovider)

    implementation(libs.apollo.runtime.v340)

    implementation("com.amplifyframework:core:0.9.0")
    implementation("com.amplifyframework:aws-api:0.9.0")

    implementation("com.apollographql.apollo3:apollo-runtime:3.4.0")
    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.github.bumptech.glide:compiler:4.12.0")

    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    implementation("net.engawapg.lib:zoomable:1.6.1")

    implementation(libs.androidx.material)
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}