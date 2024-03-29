import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven


object Plugins {
    private const val GRADLE_VERSION = "4.2.0"
    private const val KOTLIN_VERSION = "1.5.0"
    private const val HILT_VERSION = "2.35.1"
    private const val NAVIGATION_SAFE_ARGS_VERSION = "2.3.5"

    const val GRADLE = "com.android.tools.build:gradle:$GRADLE_VERSION"
    const val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_VERSION"
    const val HILT = "com.google.dagger:hilt-android-gradle-plugin:$HILT_VERSION"
    const val NAVIGATION_SAFE_ARGS = "androidx.navigation:navigation-safe-args-gradle-plugin:$NAVIGATION_SAFE_ARGS_VERSION"
}

object Build {
    const val SDK_VERSION = 30
    const val MIN_SDK_VERSION = 26
    const val TARGET_SDK_VERSION = 30
    const val BUILD_TOOLS_VERSION = "30.0.3"
    const val APPLICATION_ID = "org.xapps.apps.todox"
    const val APP_NAME = "ToDoX"
    const val MAJOR_VERSION = 1
    const val MINOR_VERSION = 0
    const val PATH_VERSION = 2
    const val STATUS_VERSION = ""
}

object Repositories {
    fun addBuildScriptRepositories(handler: RepositoryHandler) {
        handler.google()
        handler.maven(url = "https://plugins.gradle.org/m2")
    }

    fun addProjectRepositories(handler: RepositoryHandler) {
        handler.google()
        handler.jcenter()
        handler.mavenCentral()
        handler.maven(url = "https://jitpack.io")
    }
}

object Libraries {
    object Kotlin {
        private const val CORE_VERSION = "1.5.0-rc02"
        private const val COROUTINES_VERSION = "1.4.2"

        const val MODULE = "stdlib-jdk8"
        const val CORE = "androidx.core:core-ktx:$CORE_VERSION"
        const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION"
        const val COROUTINES_ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
    }

    object Jetpack {
        object MultiDex {
            private const val VERSION = "2.0.1"

            const val CORE = "androidx.multidex:multidex:$VERSION"
        }

        object UI {
            private const val CONSTRAINT_LAYOUT_VERSION = "2.0.4"
            private const val MATERIAL_VERSION = "1.3.0"
            private const val APP_COMPAT_VERSION = "1.2.0"

            const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout:$CONSTRAINT_LAYOUT_VERSION"
            const val MATERIAL = "com.google.android.material:material:$MATERIAL_VERSION"
            const val APP_COMPAT = "androidx.appcompat:appcompat:$APP_COMPAT_VERSION"
        }

        object Paging {
            private const val VERSION = "3.0.0-beta01"

            const val RUNTIME_KTX = "androidx.paging:paging-runtime-ktx:$VERSION"
        }

        object Extensions {
            private const val ACTIVITY_KOTLIN_EXT_VERSION = "1.2.2"
            private const val FRAGMENT_KOTLIN_EXT_VERSION = "1.3.3"

            const val ACTIVITY_KOTLIN_EXT = "androidx.activity:activity-ktx:$ACTIVITY_KOTLIN_EXT_VERSION"
            const val FRAGMENT_KOTLIN_EXT = "androidx.fragment:fragment-ktx:$FRAGMENT_KOTLIN_EXT_VERSION"
        }

        object Navigation {
            private const val VERSION = "2.3.5"

            const val UI_KTX = "androidx.navigation:navigation-ui-ktx:$VERSION"
            const val FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:$VERSION"
        }

        object AnnotationSupport {
            private const val ANNOTATION_VERSION = "1.2.0-rc01"
            private const val LEGACY_SUPPORT_VERSION = "1.0.0"

            const val ANNOTATION = "androidx.annotation:annotation:$ANNOTATION_VERSION"
            const val LEGACY_SUPPORT = "androidx.legacy:legacy-support-v4:$LEGACY_SUPPORT_VERSION"
        }

        object SharedPreferences {
            private const val VERSION = "1.1.1"

            const val CORE = "androidx.preference:preference:$VERSION"
            const val CORE_KTX = "androidx.preference:preference-ktx:$VERSION"
        }

        object DataStore {
            private const val VERSION = "1.0.0-beta01"

            const val BASE = "androidx.datastore:datastore-preferences:$VERSION"
        }

        object Lifecycle {
            private const val VERSION_EXTENSIONS = "2.2.0"
            private const val VERSION = "2.3.1"

            const val RUNTIME_KTX = "androidx.lifecycle:lifecycle-runtime-ktx:$VERSION"
            const val EXTENSIONS = "androidx.lifecycle:lifecycle-extensions:$VERSION_EXTENSIONS"
            const val VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
            const val LIVEDATA_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:$VERSION"
            const val VIEWMODEL_SAVED_STATE = "androidx.lifecycle:lifecycle-viewmodel-savedstate:$VERSION"
            const val COMPILER = "androidx.lifecycle:lifecycle-compiler:$VERSION"
        }

        object Room {
            private const val VERSION = "2.4.0-alpha02"

            const val RUNTIME = "androidx.room:room-runtime:$VERSION"
            const val CORE_KTX = "androidx.room:room-ktx:$VERSION"
            const val COMPILER = "androidx.room:room-compiler:$VERSION"
        }
    }

    object Dagger {
        private const val VERSION = "2.35.1"

        const val COMPILER = "com.google.dagger:dagger-compiler:$VERSION"
        const val ANDROID = "com.google.dagger:dagger-android:$VERSION"
    }

    object Hilt {
        private const val VERSION = "2.35.1"

        const val CORE = "com.google.dagger:hilt-android:$VERSION"
        const val COMPILER = "com.google.dagger:hilt-android-compiler:$VERSION"
    }

    object Permissions {
        private const val DEXTER_VERSION = "6.2.2"

        const val DEXTER = "com.karumi:dexter:$DEXTER_VERSION"
    }

    object Support {
        private const val WHATIF_VERSION = "1.0.9"

        const val WHATIF = "com.github.skydoves:whatif:$WHATIF_VERSION"
    }

    object UI {
        private const val CIRCULAR_PROGRESSBAR_VERSION = "3.0.3"
        private const val DRAWABLE_TOOLBOX_VERSION = "1.0.7"
        private const val MATERIAL_PROGRESSBAR_VERSION = "1.6.1"
        private const val TOASTY_VERSION = "1.5.0"
        private const val SWIPE_REVEAL_LAYOUT_VERSION = "1.1.0"
        private const val CALENDAR_VIEW_VERSION = "1.0.4"
        private const val FLOW_LAYOUT_VERSION = "1.2.4"

        const val CIRCULAR_PROGRESSBAR = "com.mikhaellopez:circularprogressbar:$CIRCULAR_PROGRESSBAR_VERSION"
        const val DRAWABLE_TOOLBOX = "com.github.duanhong169:drawabletoolbox:$DRAWABLE_TOOLBOX_VERSION"
        const val MATERIAL_PROGRESSBAR = "me.zhanghai.android.materialprogressbar:library:$MATERIAL_PROGRESSBAR_VERSION"
        const val TOASTY = "com.github.GrenderG:Toasty:$TOASTY_VERSION"
        const val SWIPE_REVEAL_LAYOUT = "ru.rambler.android:swipe-layout:$SWIPE_REVEAL_LAYOUT_VERSION"
        const val CALENDAR_VIEW = "com.github.kizitonwose:CalendarView:$CALENDAR_VIEW_VERSION"
        const val FLOW_LAYOUT = "com.nex3z:flow-layout:$FLOW_LAYOUT_VERSION"
    }

    object Logger {

        object Timber {
            private const val VERSION = "4.7.1"

            const val CORE = "com.jakewharton.timber:timber:$VERSION"
        }

    }

}

object Test {
    object Junit {
        private const val VERSION = "4.12"
        private const val EXT_VERSION = "1.1.1"
        const val  JUNIT = "junit:junit:$VERSION"
        const val EXT_JUNIT = "androidx.test.ext:junit:$EXT_VERSION"
    }

    object Espresso {
        private const val VERSION = "3.1.1"
        const val CORE = "androidx.test.espresso:espresso-core:$VERSION"
        const val CONTRIB = "androidx.test.espresso:espresso-contrib:$VERSION"
        const val INTENTS = "androidx.test.espresso:espresso-intents:$VERSION"
    }

    object Coroutines {
        private const val VERSION = "1.3.7"
        const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$VERSION"
    }
}
