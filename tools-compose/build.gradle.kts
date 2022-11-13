android {
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = "1.3.2"
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2022.10.00"))
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
