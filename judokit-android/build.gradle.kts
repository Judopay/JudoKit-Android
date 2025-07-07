import com.judopay.judokit.Namespaces
import com.judopay.judokit.Versions

plugins {
    id("judokit.android.library")
    id("judokit.android.kotlin")
    id("judokit.android.lint")
    id("judokit.maven-publish")
}

android {
    namespace = Namespaces.LIBRARY
    version = Versions.VERSION_NAME
}

dependencies {
    // For the UI configuration classes
    api(libs.judopay.judo3ds2)
    implementation(libs.judopay.device.dna)

    implementation(libs.bundles.kotlinx.coroutines)
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.androidx.navigation)

    // For the exposed API client callbacks interfaces
    api(libs.squareup.retrofit2)
    implementation(libs.squareup.retrofit2.converter.gson)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.vectordrawable)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.activity.ktx)

    implementation(libs.android.material)

    implementation(libs.play.services.wallet)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)

    compileOnly(libs.ravelin.encrypt)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.vintage.engine)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testImplementation(libs.squareup.okhttp3.mockwebserver)
    testImplementation(platform(libs.squareup.okhttp3.bom))
    testImplementation(libs.mockk)
    testImplementation(libs.google.truth)
    testImplementation(libs.androidx.core.testing)

    testImplementation(project(":judokit-android"))
}
