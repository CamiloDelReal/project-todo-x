import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.BaseVariantOutput
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat


plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdkVersion(Build.SDK_VERSION)
    buildToolsVersion = Build.BUILD_TOOLS_VERSION

    defaultConfig {
        applicationId = Build.APPLICATION_ID

        minSdkVersion(Build.MIN_SDK_VERSION)
        targetSdkVersion(Build.TARGET_SDK_VERSION)

        versionCode =
                ((Build.MAJOR_VERSION * 10000) + (Build.MINOR_VERSION * 100) + Build.PATH_VERSION)
        versionName =
                "${Build.MAJOR_VERSION}.${Build.MINOR_VERSION}.${Build.PATH_VERSION}${Build.STATUS_VERSION}"

//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true

        multiDexEnabled = true
//        dexOptions.incremental = true
        dexOptions.preDexLibraries = false
        dexOptions.javaMaxHeapSize = "4g"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    (this as ExtensionAware).configure < KotlinJvmOptions > {
        jvmTarget = "1.8"
    }

    dataBinding.isEnabled = true

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
        getByName("debug").java.srcDirs("src/debug/kotlin")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )

            applicationVariants.all(object: Action<ApplicationVariant> {
                override fun execute(variant: ApplicationVariant) {
                    variant.outputs.all(object: Action<BaseVariantOutput> {
                        override fun execute(output: BaseVariantOutput) {
                            val outputImpl = output as BaseVariantOutputImpl
                            val currentDate = Date()
                            val formatter = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
                            val timestamp = formatter.format(currentDate)
                            val fileName = "${Build.APP_NAME} ${Build.MAJOR_VERSION}.${Build.MINOR_VERSION}.${Build.PATH_VERSION}${Build.STATUS_VERSION} $timestamp.apk"
                            outputImpl.outputFileName = fileName
                        }
                    })
                }
            })
        }
    }
}

dependencies {
    // Custom JARs
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Kotlin
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation(Libraries.Kotlin.CORE)
    implementation(Libraries.Kotlin.COROUTINES)

    // Jetpack MultiDex
    implementation(Libraries.Jetpack.MultiDex.CORE)

    // Jetpack Annotations Support
    kapt(Libraries.Jetpack.AnnotationSupport.ANNOTATION)
    implementation(Libraries.Jetpack.AnnotationSupport.LEGACY_SUPPORT)

    // Jetpack UI
    implementation(Libraries.Jetpack.UI.CONSTRAINT_LAYOUT)
    implementation(Libraries.Jetpack.UI.MATERIAL)
    implementation(Libraries.Jetpack.UI.APP_COMPAT)
    implementation(Libraries.Jetpack.UI.RECYCLER_VIEW)
    implementation(Libraries.Jetpack.UI.CARD_VIEW)

    // Jetpack Extensions
    implementation(Libraries.Jetpack.Extensions.ACTIVITY_KOTLIN_EXT)
    implementation(Libraries.Jetpack.Extensions.FRAGMENT_KOTLIN_EXT)

    // Jetpack Navigation
    implementation(Libraries.Jetpack.Navigation.UI_KTX)
    implementation(Libraries.Jetpack.Navigation.FRAGMENT_KTX)

    // Jetpack Shared Preferences
    implementation(Libraries.Jetpack.SharedPreferences.CORE)

    // Jetpack Lifecycle
    implementation(Libraries.Jetpack.Lifecycle.RUNTIME)
    implementation(Libraries.Jetpack.Lifecycle.EXTENSIONS)
    implementation(Libraries.Jetpack.Lifecycle.VIEWMODEL_SAVED_STATE)
    implementation(Libraries.Jetpack.Lifecycle.VIEWMODEL_KTX)
    kapt(Libraries.Jetpack.Lifecycle.COMPILER)

    // Jetpack Room
    implementation(Libraries.Jetpack.Room.RUNTIME)
    kapt(Libraries.Jetpack.Room.COMPILER)
    implementation(Libraries.Jetpack.Room.RXJAVA)

    // Dagger
    implementation(Libraries.Dagger.CORE)
    kapt(Libraries.Dagger.COMPILER)
    implementation(Libraries.Dagger.ANDROID)
    implementation(Libraries.Dagger.ANDROID_SUPPORT)
    kapt(Libraries.Dagger.ANDROID_PROCESSOR)

    // Scalable Units
    implementation(Libraries.ScalableUnits.DP)
    implementation(Libraries.ScalableUnits.SP)

    // UI
    implementation(Libraries.UI.LOOPING_VIEW_PAGER)
    implementation(Libraries.UI.PAGE_INDICATOR_VIEW)
//    implementation(Libraries.UI.MATERIAL_PROGRESS_BAR)
    implementation(Libraries.UI.PICASSO)
//    implementation(Libraries.UI.UCROP)
    implementation(Libraries.UI.ACTIVITY_CIRCULAR_REVEAL)

    // Test
//    implementation(Test.Junit)
//    implementation(Test.Espresso.CORE)
//    implementation(Test.Espresso.CONTRIB)
//    implementation(Test.Espresso.INTENTS)

    implementation("me.everything:overscroll-decor-android:1.0.4")
}
