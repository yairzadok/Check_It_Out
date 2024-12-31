plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "yair.tzadok.importfilefromexcle_realtimefb"
    compileSdk = 34

    defaultConfig {
        applicationId = "yair.tzadok.importfilefromexcle_realtimefb"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.camera.view)
    implementation(libs.play.services.ads.lite)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Apache POI library tools
    implementation (libs.poi)
    implementation (libs.poi.ooxml)

    // for the use of creating QR add these lines to the gradle
    implementation(libs.zxing.embedded)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.multidex)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    // for scanning  any QR
    implementation(libs.zxing.core)
   implementation(libs.zxing.embedded)
// https://developer.android.com/media/camera/camerax


    val camerax_version = "1.2.2"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation (libs.camera.core.v122)
    implementation (libs.camera.camera2.v122)
    // If you want to additionally use the CameraX Lifecycle library
    implementation (libs.camera.lifecycle.v122)
    // If you want to additionally use the CameraX View class
    implementation (libs.camera.view.v132)
    // CameraX core library
    implementation ("androidx.camera:camera-core:1.1.0-alpha07")
    implementation ("androidx.camera:camera-camera2:1.1.0-alpha07")
    implementation ("androidx.camera:camera-lifecycle:1.1.0-alpha07")
    implementation ("androidx.camera:camera-view:1.0.0-alpha31")
    // Required for CameraX extensions
    implementation ("androidx.camera:camera-extensions:1.0.0-alpha31")


    // If you want to additionally use the CameraX Extensions library
    implementation ("androidx.camera:camera-extensions:1.3.2")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.11.0")

    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    //
    implementation ("androidx.concurrent:concurrent-futures:1.1.0")

    testImplementation ("junit:junit:4.+")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")


    // https://developers.google.com/ml-kit/vision/barcode-scanning

    // 1st way = bar code scanning - without google play services
    implementation ("com.google.mlkit:barcode-scanning:17.3.0")

    // 2nd way = bar code scanning - with google play services
    implementation ("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.1")


}

apply(plugin = "com.google.gms.google-services")
