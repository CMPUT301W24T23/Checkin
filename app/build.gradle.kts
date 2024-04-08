import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.checkin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.checkin"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packaging {
        resources {
            excludes.add("META-INF/DEPENDENCIES")
        }
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }


    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets")
            }
        }
    }
    buildFeatures {
        viewBinding = true
    }
}



dependencies {

    //implementation(files("/Users/samirasalman/Library/Android/sdk/platforms/android-34/android.jar")) // need for creating javadocs
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.google.android.gms:play-services-maps:17.0.0")
    implementation("com.google.android.gms:play-services-location:18.0.0")
    implementation("com.google.android.gms:play-services-maps:17.0.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    debugImplementation("androidx.fragment:fragment-testing:1.6.2")
    implementation("androidx.test.espresso:espresso-intents:3.5.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation("com.google.firebase:firebase-firestore")
    androidTestImplementation("com.jayway.android.robotium:robotium-solo:5.3.1")
    androidTestImplementation("org.mockito:mockito-android:5.11.0")
    androidTestImplementation("org.mockito:mockito-core:5.11.0")
    implementation("androidx.preference:preference:1.2.1")
    implementation ("com.google.firebase:firebase-messaging:23.4.1")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.17.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")


}