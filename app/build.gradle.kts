import java.util.Properties

/**
 * Hugging Face read token for gated models (e.g. Gemma), baked into BuildConfig.
 * Precedence: repo-root `local.properties` key `HUGGINGFACE_READ_TOKEN`, then repo-root `.env`.
 *
 * `.env` supports:
 * - `HUGGINGFACE_READ_TOKEN=hf_…` or `HF_TOKEN=hf_…` (optional quotes)
 * - a single bare line `hf_…` (entire file)
 */
val huggingFaceReadToken: String = run {
    val lp = rootProject.file("local.properties")
    val fromLocal = if (lp.exists()) {
        Properties().apply { lp.inputStream().use { load(it) } }
            .getProperty("HUGGINGFACE_READ_TOKEN", "")
            .trim()
    } else {
        ""
    }
    if (fromLocal.isNotEmpty()) return@run fromLocal

    val envFile = rootProject.file(".env")
    if (!envFile.exists()) return@run ""

    val lines = envFile.readLines()
    for (line in lines) {
        val t = line.trim()
        if (t.isEmpty() || t.startsWith("#")) continue
        val m = Regex("""^(?:export\s+)?(?:HUGGINGFACE_READ_TOKEN|HF_TOKEN)\s*=\s*(.+)$""").find(t)
            ?: continue
        var v = m.groupValues[1].trim()
        if (v.length >= 2) {
            if (v.startsWith("\"") && v.endsWith("\"")) v = v.substring(1, v.length - 1)
            if (v.startsWith("'") && v.endsWith("'")) v = v.substring(1, v.length - 1)
        }
        if (v.isNotEmpty()) return@run v.trim()
    }

    val firstMeaningful = lines.map { it.trim() }.firstOrNull { it.isNotEmpty() && !it.startsWith("#") }
        ?: ""
    if (firstMeaningful.startsWith("hf_") && !firstMeaningful.contains("=")) {
        return@run firstMeaningful
    }

    val whole = envFile.readText().trim()
    if (whole.startsWith("hf_") && !whole.contains("=")) return@run whole

    ""
}

configurations.configureEach {
    resolutionStrategy {
        force("androidx.core:core-ktx:1.15.0")
        force("androidx.core:core:1.15.0")
    }
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kover)
}



android {
    namespace = "com.ksetrasevakah"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ksetrasevakah"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val escapedHfToken = huggingFaceReadToken
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
        buildConfigField("String", "HUGGINGFACE_READ_TOKEN", "\"$escapedHfToken\"")
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/DEPENDENCIES"
        }
    }

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    sourceSets.getByName("androidTest").assets.srcDir("src/androidTest/assets")
}

tasks.register<Copy>("syncRoomSchemasForAndroidTest") {
    from(layout.projectDirectory.dir("schemas/com.ksetrasevakah.core.database.KsetraDatabase"))
    into(
        layout.projectDirectory.dir(
            "src/androidTest/assets/schemas/com.ksetrasevakah.core.database.KsetraDatabase"
        )
    )
}
tasks.named("preBuild").configure { dependsOn("syncRoomSchemasForAndroidTest") }

dependencies {
    // Compose BOM
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Navigation
    implementation(libs.compose.navigation)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // WorkManager
    implementation(libs.workmanager)

    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)

    // Core
    implementation(libs.activity.compose)
    implementation(libs.core.ktx)
    implementation(libs.core.splashscreen)
    implementation(libs.accompanist.permissions)
    implementation(libs.play.services.auth)
    implementation(libs.google.api.client.android)
    implementation(libs.google.api.services.drive)
    implementation(libs.datastore.preferences)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // On-device LLM (LiteRT-LM, GPU via OpenCL/Adreno) + model download
    implementation(libs.litertlm.android)
    implementation(libs.okhttp)
    implementation(libs.onnxruntime.android)

    // Unit Testing
    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
    testImplementation(libs.junit5.params)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.coroutines.test)

    // Android Testing
    androidTestImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.mockk.android)
}
