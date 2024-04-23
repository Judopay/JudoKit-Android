import com.judopay.judokit.Namespaces
import com.judopay.judokit.Versions

plugins {
    id("judokit.android.application")
    id("judokit.android.kotlin")
    id("judokit.android.lint")
    id("judokit.firebase")
}

android {
    namespace = Namespaces.EXAMPLE_APP

    defaultConfig {
        applicationId = Namespaces.EXAMPLE_APP
        versionName = Versions.VERSION_NAME
    }
}

dependencies {
    implementation(project(":judokit-android"))

    implementation(libs.kotlin.reflect)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.android.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.chucker)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.squareup.retrofit2)
    implementation(libs.squareup.retrofit2.converter.gson)
    implementation(libs.ravelin.encrypt)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // https://issuetracker.google.com/issues/122321150
    debugImplementation(libs.androidx.junit.ktx)
    debugImplementation(libs.androidx.espresso.contrib)

    androidTestImplementation(libs.gson)
    androidTestImplementation(libs.bundles.androidx.espresso)
    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.room.runtime)
}
