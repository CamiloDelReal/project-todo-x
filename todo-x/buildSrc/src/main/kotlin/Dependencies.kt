import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

object Plugins {
    private const val GRADLE_VERSION = "3.5.3"
    private const val KOTLIN_VERSION = "1.3.70"
    private const val NAVIGATION_SAFE_ARGS_VERSION = "2.2.1"

    const val GRADLE = "com.android.tools.build:gradle:$GRADLE_VERSION"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_VERSION"
    const val NAVIGATION_SAFE_ARGS = "androidx.navigation:navigation-safe-args-gradle-plugin:$NAVIGATION_SAFE_ARGS_VERSION"
}

object Build {
    const val SDK_VERSION = 29
    const val MIN_SDK_VERSION = 21
    const val TARGET_SDK_VERSION = 29
    const val BUILD_TOOLS_VERSION = "29.0.2"
    const val APPLICATION_ID = "org.xapps.apps.todox"
    const val APP_NAME = "ToDoX"
    const val MAJOR_VERSION = 1
    const val MINOR_VERSION = 0
    const val PATH_VERSION = 0
    const val STATUS_VERSION = ""
}

object Repositories {
    fun addBuildScriptRepositories(handler: RepositoryHandler) {
        handler.google()
        handler.jcenter()
        //handler.gradlePluginPortal()  // Unstable
        handler.maven(url = "https://plugins.gradle.org/m2")
        handler.maven(url = "https://maven.fabric.io/public")
        handler.maven(url = "https://clojars.org/repo/")
        handler.maven(url = "https://repo1.maven.org/maven2")
        handler.maven(url = "https://jitpack.io")
    }

    fun addProjectRepositories(handler: RepositoryHandler) {
        handler.google()
        handler.jcenter()
        handler.mavenCentral()
        handler.maven(url = "https://clojars.org/repo/")
        handler.maven(url = "https://repo1.maven.org/maven2")
        handler.maven(url = "https://jitpack.io")
    }
}

object Libraries {
    object Kotlin {
        private const val CORE_VERSION = "1.1.0"
        private const val COROUTINES_VERSION = "1.3.3"

        const val CORE = "androidx.core:core-ktx:$CORE_VERSION"
        const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
    }

    object Jetpack {
        object MultiDex {
            private const val VERSION = "2.0.1"

            const val CORE = "androidx.multidex:multidex:$VERSION"
        }

        object UI {
            private const val CONSTRAINT_LAYOUT_VERSION = "1.1.3"
            private const val MATERIAL_VERSION = "1.2.0-alpha04"
            private const val APP_COMPAT_VERSION = "1.1.0"
            private const val RECYCLER_VIEW_VERSION = "1.1.0"
            private const val CARD_VIEW_VERSION = "1.0.0"

            const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:$CONSTRAINT_LAYOUT_VERSION"
            const val MATERIAL = "com.google.android.material:material:$MATERIAL_VERSION"
            const val APP_COMPAT = "androidx.appcompat:appcompat:$APP_COMPAT_VERSION"
            const val RECYCLER_VIEW = "androidx.recyclerview:recyclerview:$RECYCLER_VIEW_VERSION"
            const val CARD_VIEW = "androidx.cardview:cardview:$CARD_VIEW_VERSION"
        }
        
        object Extensions {
            private const val ACTIVITY_KOTLIN_EXT_VERSION = "1.1.0"
            private const val FRAGMENT_KOTLIN_EXT_VERSION = "1.2.2"

            const val ACTIVITY_KOTLIN_EXT = "androidx.activity:activity-ktx:$ACTIVITY_KOTLIN_EXT_VERSION"
            const val FRAGMENT_KOTLIN_EXT = "androidx.fragment:fragment-ktx:$FRAGMENT_KOTLIN_EXT_VERSION"
        }

        object Navigation {
            private const val VERSION = "2.2.0"

            const val UI = "androidx.navigation:navigation-ui:$VERSION"
            const val UI_KTX = "androidx.navigation:navigation-ui-ktx:$VERSION"
            const val FRAGMENT = "androidx.navigation:navigation-fragment:$VERSION"
            const val FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:$VERSION"
        }

        object AnnotationSupport {
            private const val ANNOTATION_VERSION = "1.1.0"
            private const val LEGACY_SUPPORT_VERSION = "1.0.0"

            const val ANNOTATION = "androidx.annotation:annotation:$ANNOTATION_VERSION"
            const val LEGACY_SUPPORT = "androidx.legacy:legacy-support-v4:$LEGACY_SUPPORT_VERSION"
        }

        object SharedPreferences {
            private const val VERSION = "1.1.0"

            const val CORE = "androidx.preference:preference:$VERSION"
        }

        object Lifecycle {
            private const val VERSION = "2.2.0"

            const val RUNTIME = "androidx.lifecycle:lifecycle-runtime:$VERSION"
            const val EXTENSIONS = "androidx.lifecycle:lifecycle-extensions:$VERSION"
            const val VIEWMODEL = "androidx.lifecycle:lifecycle-viewmodel:$VERSION"
            const val VIEWMODEL_SAVED_STATE = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$VERSION"
            const val VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
            const val COMPILER = "androidx.lifecycle:lifecycle-compiler:$VERSION"
        }

        object Room {
            private const val VERSION = "2.2.3"

            const val RUNTIME = "androidx.room:room-runtime:$VERSION"
            const val CORE_KTX = "androidx.room:room-ktx:$VERSION"
            const val COMPILER = "androidx.room:room-compiler:$VERSION"
            const val RXJAVA = "androidx.room:room-rxjava2:$VERSION"
        }
    }

    object Dagger {
        private const val VERSION = "2.26"

        const val CORE = "com.google.dagger:dagger:$VERSION"
        const val COMPILER = "com.google.dagger:dagger-compiler:$VERSION"
        const val ANDROID = "com.google.dagger:dagger-android:$VERSION"
        const val ANDROID_SUPPORT = "com.google.dagger:dagger-android-support:$VERSION"
        const val ANDROID_PROCESSOR = "com.google.dagger:dagger-android-processor:$VERSION"
    }

    object Retrofit {
        private const val VERSION = "2.7.1"
        private const val LOGGING_INTERCEPTOR_VERSION = "4.3.1"
        private const val MOSHI_KTX_VERSION = "1.9.2"

        const val CORE = "com.squareup.retrofit2:retrofit:$VERSION"
        const val MOSHI = "com.squareup.retrofit2:converter-moshi:$VERSION"
        const val GSON = "com.squareup.retrofit2:converter-gson:$VERSION"
        const val SCALARS = "com.squareup.retrofit2:converter-scalars:$VERSION"
        const val RXJAVA = "com.squareup.retrofit2:adapter-rxjava2:$VERSION"
        const val LOGGING_INTERCEPTOR = "com.squareup.okhttp3:logging-interceptor:$LOGGING_INTERCEPTOR_VERSION"
        const val MOSHI_KTX = "com.squareup.moshi:moshi-kotlin:$MOSHI_KTX_VERSION"
    }

    object ScalableUnits {
        private const val VERSION = "1.0.6"

        const val DP = "com.intuit.sdp:sdp-android:$VERSION"
        const val SP = "com.intuit.ssp:ssp-android:$VERSION"
    }

    object UI {
        private const val LOOPING_VIEW_PAGER_VERSION = "1.2.0"
        private const val PAGE_INDICATOR_VIEW_VERSION = "1.0.3"
        private const val PICASSO_VERSION = "2.71828"
        private const val UCROP_VERSION = "2.2.2"
        private const val MP_ANDROID_CHART_VERSION = "3.1.0"
        private const val FLOW_LAYOUT_VERSION = "0.4.1"
        private const val PROGRESS_BUTTON_VERSION = "1.0.3"
        private const val MATERIAL_PROGRESS_BAR_VERSION = "1.6.1"
        private const val ACTIVITY_CIRCULAR_REVEAL_VERSION = "1.0.1"

        const val LOOPING_VIEW_PAGER = "com.asksira.android:loopingviewpager:$LOOPING_VIEW_PAGER_VERSION"
        const val PAGE_INDICATOR_VIEW = "com.romandanylyk:pageindicatorview:$PAGE_INDICATOR_VIEW_VERSION"
        const val PICASSO = "com.squareup.picasso:picasso:$PICASSO_VERSION"
        const val UCROP = "com.github.yalantis:ucrop:$UCROP_VERSION"
        const val MP_ANDROID_CHART = "com.github.PhilJay:MPAndroidChart:$MP_ANDROID_CHART_VERSION"
        const val FLOW_LAYOUT = "com.wefika:flowlayout:$FLOW_LAYOUT_VERSION"
        const val PROGRESS_BUTTON = "com.github.razir.progressbutton:progressbutton:$PROGRESS_BUTTON_VERSION"
        const val MATERIAL_PROGRESS_BAR = "me.zhanghai.android.materialprogressbar:library:$MATERIAL_PROGRESS_BAR_VERSION"
        const val ACTIVITY_CIRCULAR_REVEAL = "com.github.tombayley:ActivityCircularReveal:$ACTIVITY_CIRCULAR_REVEAL_VERSION"
    }

    object QRs {
        private const val ZXING_VERSION = "3.4.0"
        private const val ZXING_ANDROID_EMBEDDED_VERSION = "4.1.0"

        const val ZXING = "com.google.zxing:core:$ZXING_VERSION"
        const val ZXING_ANDROID_EMBEDDED = "com.journeyapps:zxing-android-embedded:$ZXING_ANDROID_EMBEDDED_VERSION"
    }

    object Permissions {
        private const val DEXTER_VERSION = "6.0.2"

        const val DEXTER = "com.karumi:dexter:$DEXTER_VERSION"
    }
}

object Test {
    object Junit {
        private const val VERSION = "4.12"
        const val JUNIT = "junit:junit:$VERSION"
    }

    object Espresso {
        private const val VERSION = "3.1.1"
        const val CORE = "androidx.test.espresso:espresso-core:$VERSION"
        const val CONTRIB = "androidx.test.espresso:espresso-contrib:$VERSION"
        const val INTENTS = "androidx.test.espresso:espresso-intents:$VERSION"
    }
}