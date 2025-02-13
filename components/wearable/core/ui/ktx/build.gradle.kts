plugins {
    id("flipper.android-compose")
}

android.namespace = "com.flipperdevices.wearable.core.ui.ktx"

dependencies {

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.tooling)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.wear.foundation)
    implementation(libs.compose.wear.material)
}
