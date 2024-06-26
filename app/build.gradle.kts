plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.example.chat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chat"
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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.gson)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Scalable Size Unit (support for different screen sizes)
    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    //Rounded ImageView
    implementation(libs.roundedimageview)
    implementation (libs.lottie)

    //Retrofit 网络请求
    implementation(libs.retrofit)
    implementation(libs.converter.gson)


    // PictureSelector 基础 (必须)
    implementation (libs.pictureselector)
    // 图片压缩 (按需引入)
    implementation (libs.compress)
    // 图片裁剪 (按需引入
    implementation (libs.ucrop)
    // 自定义相机 (按需引入)
    implementation (libs.camerax)
    // 文件操作 FileOperator
    implementation (libs.file.core)
    implementation (libs.glide)
}