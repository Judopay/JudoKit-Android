package com.judopay.judokit

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project

/*
* Classes that require the Android runtime and cannot be exercised in JVM unit tests.
* Referenced by both the instrumentation filter (controls which classes are instrumented)
* and the report filter (controls which classes appear in coverage reports) below.
*/
private val KOVER_EXCLUDED_CLASSES =
    listOf(
        // Generated code
        "*.databinding.*",
        "*.BuildConfig",

        // Android UI components (Activities, Fragments, Views)
        "*Activity*",
        "*Fragment*",
        "*.ui.cardentry.components.*",
        "*.ui.cardentry.formatting.*",
        "*.ui.paymentmethods.components.*",
        "*.ui.paymentmethods.adapter.viewholder.*",
        "*.ui.paymentmethods.adapter.PaymentMethodsAdapter*",
        "*.ui.paymentmethods.adapter.SwipeToDeleteCallback*",
        "*.ui.editcard.adapter.*",
        "*.ui.common.LengthFilter*",
        "*.ui.common.GooglePayButton*",
        "*.ui.common.ProgressButton*",
        "*.ui.common.MappersKt",

        // Room database (requires Android runtime)
        "*.db.JudoRoomDatabase*",
        "*.db.dao.*",

        // Android-heavy service classes
        "*JudoCardTransactionClient*",
        "*JudoGooglePayService*",
        "*.service.PayloadService*",
        "*.service.CardTransactionRepository*",
        "*.service.RecommendationService*",
        "*.service.RecommendationServiceKt",

        // Extension functions requiring 3DS SDK Transaction or Android Context
        "*.model.TransactionDetailsKt",

        // Interceptors requiring Android Context or device-specific SDKs
        "*.api.interceptor.DeviceDnaInterceptor*",
        "*.api.interceptor.PayLoadInterceptor*",

        // API service factories requiring Android Context / SSL setup
        "*.api.factory.JudoApiServiceFactory*",
        "*.api.factory.RecommendationApiServiceFactory*",
        "*.api.factory.ServiceFactory*",
        "*.api.factory.Tls12SslSocketFactory*",

        // UI helpers requiring Android Context (animations, ContextCompat colours)
        "*.ui.paymentmethods.model.CardAnimationTypeKt",
        "*.ui.editcard.CardPatternKt",

        // Anonymous lambda classes
        "*\$viewModelFactory$1",
        "*\$runOnFirstAttach$1",
    )

internal fun Project.configureKover(
    extension: KoverProjectExtension,
) = extension.apply {
    currentProject {
        instrumentation {
            excludedClasses.addAll(KOVER_EXCLUDED_CLASSES)
        }
    }

    reports {
        total {
            xml {
                onCheck.set(true)
                xmlFile.set(layout.buildDirectory.file("reports/kover/jacocoTestReport.xml"))
            }
        }
        filters {
            excludes {
                classes(KOVER_EXCLUDED_CLASSES)
            }
        }
    }
}
