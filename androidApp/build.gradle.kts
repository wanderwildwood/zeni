
import org.codehaus.groovy.runtime.ProcessGroovyMethods
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.aboutlibraries.android)
}

// https://developer.android.com/studio/publish/app-signing#secure-key
// Create a variable called keystorePropertiesFile, and initialize it to your
// keystore.properties file, in the rootProject folder.
// The house rule: a real key in signing/ (gitignored) signs release builds; without it a
// release comes out unsigned. No fallback key.
val signingPropertiesFile = rootProject.file("signing/signing.properties")
val signingProperties = Properties().apply {
    if (signingPropertiesFile.isFile) signingPropertiesFile.inputStream().use(::load)
}

// Load local.properties
val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val appKeys = rootProject.file("app_keys.txt").takeIf { it.exists() }?.readText() ?: ""

android {
    namespace = "com.blockstream.green"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 8
        versionName = "0.1.7"

        base.archivesName = "zeni-v$versionName"
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

        testApplicationId = "com.blockstream.green.test"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            if (signingPropertiesFile.isFile) {
                storeFile = rootProject.file("signing/signing.keystore")
                storePassword = signingProperties.getProperty("STORE_PASSWORD")
                keyAlias = signingProperties.getProperty("KEY_ALIAS")
                keyPassword = signingProperties.getProperty("KEY_PASSWORD")
            }
        }
    }

    flavorDimensions += listOf("normal")
    productFlavors {
        create("development") {
            applicationId = "com.greenaddress.greenbits_android_wallet.dev"
            versionNameSuffix  = "-dev" + appendGdkCommitHash(rootProject, false)
            resValue("string", "app_name", "Bitcoin Dev")
            resValue("string", "application_id", applicationId!!)
            resValue("bool", "feature_lightning", "true")
            resValue("bool", "feature_analytics", "false")
            resValue("bool", "feature_rate_google_play", "true")
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher_dev"
            manifestPlaceholders["appIconRound"] = "@mipmap/ic_launcher_dev_round"

            // Development PIN code from local.properties
            val devPinCode = localProperties.getProperty("DEV_PIN_CODE") ?: ""
            buildConfigField("String", "DEV_PIN_CODE", """"$devPinCode"""")
            buildConfigField("String", "APP_KEYS", """"$appKeys"""")
        }

        create("productionGoogle") {
            applicationId = "com.greenaddress.greenbits_android_wallet"
            resValue("string", "app_name", "Bitcoin")
            resValue("string", "application_id", applicationId!!)
            resValue("bool", "feature_lightning", "true")
            resValue("bool", "feature_analytics", "false")
            resValue("bool", "feature_rate_google_play", "true")
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher"
            manifestPlaceholders["appIconRound"] = "@mipmap/ic_launcher_round"

            // No development PIN for production
            buildConfigField("String", "DEV_PIN_CODE", "null")
            buildConfigField("String", "APP_KEYS", """"$appKeys"""")

            ndk {
                abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
            }
        }

        create("productionFDroid") {
            applicationId = "com.wanderwildwood.zeni"
            resValue("string", "app_name", "Bitcoin")
            resValue("string", "application_id", applicationId!!)
            resValue("bool", "feature_lightning", "false")
            resValue("bool", "feature_analytics", "false")
            resValue("bool", "feature_rate_google_play", "false")
            manifestPlaceholders["appIcon"] = "@drawable/ic_launcher_zeni"
            manifestPlaceholders["appIconRound"] = "@drawable/ic_launcher_zeni"

            // No development PIN for production
            buildConfigField("String", "DEV_PIN_CODE", "null")
            buildConfigField("String", "APP_KEYS", """"$appKeys"""")

            // zeni: the Kompakt is 64-bit ARM only; -PabiFilter=x86_64 builds for the emulator.
            ndk {
                abiFilters.add(providers.gradleProperty("abiFilter").getOrElse("arm64-v8a"))
            }
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            matchingFallbacks += listOf("normal")

            signingConfigs.getByName("release").also {
                if(it.storeFile != null){
                    signingConfig = it
                }
            }
        }
    }
    compileOptions {
        // SDK 23 support
        isCoreLibraryDesugaringEnabled = true
    }
    packaging {
        jniLibs.pickFirsts.add("**/*.so")
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
    lint {
        abortOnError = false
        disable += listOf("MissingTranslation", "SpUsage", "Instantiatable")
        ignoreWarnings = false
    }
}


// For KSP, configure using KSP extension:
ksp {
    arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xskip-prerelease-check")
    }

    jvmToolchain(libs.versions.jvm.get().toInt())
    
    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlin.io.encoding.ExperimentalEncodingApi")
                optIn("kotlin.ExperimentalUnsignedTypes")
                optIn("kotlinx.coroutines.FlowPreview")
                optIn("kotlin.time.ExperimentalTime")
            }
        }
    }
}

val developmentImplementation by configurations
val productionGoogleImplementation by configurations
val productionFDroidImplementation by configurations

dependencies {
    implementation(project(":base-gms"))
    /**  --- Modules ---------------------------------------------------------------------------- */
    implementation(project(":compose"))

    developmentImplementation(project(":gms"))
    productionGoogleImplementation(project(":gms"))
    productionFDroidImplementation(project(":no-gms")) // F-Droid
    /** ----------------------------------------------------------------------------------------- */

    /**  --- Java 8+ API desugaring support ----------------------------------------------------- */
    coreLibraryDesugaring(libs.desugar)
    /** ----------------------------------------------------------------------------------------- */

    /**  --- Room ------------------------------------------------------------------------------- */
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    /** ----------------------------------------------------------------------------------------- */

    /**  --- Koin   ----------------------------------------------------------------------------- */
    // For instrumentation tests
    androidTestImplementation(platform(libs.koin.bom))
    androidTestImplementation(libs.koin.test)
    // androidTestImplementation(libs.koin.test.junit4)

    // For local unit tests
    testImplementation(libs.koin.test)
    // testImplementation(libs.koin.test.junit4)
    /** ----------------------------------------------------------------------------------------- */

    testImplementation(libs.junit)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockk)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

fun appendGdkCommitHash(project: Project, enableGitSubmodule: Boolean): String{
    val gdkCommit = System.getenv("GDK_COMMIT")
    val gdkCommitFile = project.file("gdk/gdk_commit")
    var hash: String? = null

    if (!gdkCommit.isNullOrBlank()) {
        hash = gdkCommit
    } else if (gdkCommitFile.exists()){
        val content = gdkCommitFile.readText().trim()
        hash = content.substring(0, 8.coerceAtMost(content.length))
    } else if (enableGitSubmodule) {
        val cmd = "git --git-dir=gdk/gdk/.git rev-parse --short HEAD"
        val proc = ProcessGroovyMethods.execute(cmd)
        hash = ProcessGroovyMethods.getText(proc).trim()
    }

    return hash?.takeIf { it.isNotBlank() }?.let { "-gdk:${it}" } ?: ""
}

task("verifyDependencies", GradleBuild::class) {
    tasks = listOf("lintDevelopmentRelease", "assembleProductionRelease")
}

// Disable Google Services Plugin for FDroid flavor
afterEvaluate {
    tasks.matching {
        it.name.contains("FDroid") && it.name.contains("GoogleServices")
    }.all {
        enabled = false
    }
}

class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File
) : CommandLineArgumentProvider {

    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}

// The Licenses page lists what the build actually packs: this plugin walks each variant's runtime
// dependencies and writes res/raw/aboutlibraries.json (one entry per artifact, with its POM's
// licence). What Gradle cannot see (fonts, data sets, native code, desugaring) and licence texts
// of our own choosing are in config/libraries and config/licenses. Offline: the build fetches
// nothing; the full texts come from res/raw and config/licenses. The app reads the file with
// shared-licences' LicencesJson.kt (no AboutLibraries runtime library).
aboutLibraries {
    offlineMode.set(true)
    collect {
        configPath.set(rootProject.file("config"))
        fetchRemoteLicense.set(false)
        fetchRemoteFunding.set(false)
        includePlatform.set(false)
    }
    export {
        excludeFields.addAll("funding")
    }
    library {
        mergePlatformArtifacts.set(true)
    }
}

// The build fails when a bundled library has no license, or a license with no full text to show
// (neither its own text from config/licenses nor a family text in res/raw). Mudita review, 4 Nov 2026.
// zeni's family texts are Compose resources of the compose module (files/*.txt), read by the page.
val licenceRawTexts: List<String> = rootProject.file("compose/src/commonMain/composeResources/files").listFiles().orEmpty()
    .map { it.name.lowercase() }.filter { it.endsWith(".txt") }
tasks.matching { it.name.startsWith("prepareLibraryDefinitions") }.configureEach {
    val raw = licenceRawTexts
    inputs.property("licenceRawTexts", raw) // so the check reruns when a text is added or removed
    doLast {
        fun family(n: String): String? {
            val l = n.lowercase()
            return when {
                "classpath" in l -> "classpath"; "lgpl" in l || "lesser general" in l -> "lgpl"
                "gpl" in l || "general public licen" in l -> "gpl"; "unicode" in l -> "unicode"; "apache" in l -> "apache"
                "mpl" in l || "mozilla" in l -> "mpl"; "mit" in l.split(Regex("[^a-z]+")) -> "mit"; "bsd" in l -> "bsd"
                "ofl" in l || "open font" in l -> "ofl"; "isc" in l.split(Regex("[^a-z]+")) -> "isc"; "cc-by" in l || "cc by" in l || "cc0" in l || "public domain" in l -> "none"
                else -> null
            }
        }
        val bad = mutableListOf<String>()
        outputs.files.asFileTree.matching { include("**/aboutlibraries.json") }.forEach { f ->
            @Suppress("UNCHECKED_CAST")
            val root = groovy.json.JsonSlurper().parse(f) as MutableMap<String, Any?>
            val lics = (root["licenses"] as? Map<String, Map<String, Any?>>).orEmpty()
            // A library's own license file (config/licenses, with its copyright line) stands in for
            // the bare family entry its POM names: the plugin merges config entries in, it never
            // takes one out, so a library would otherwise list "MIT" twice, once with no text.
            var changed = false
            (root["libraries"] as? List<MutableMap<String, Any?>>).orEmpty().forEach { lib ->
                val keys = (lib["licenses"] as? List<String>).orEmpty()
                fun fam(k: String) = family((lics[k]?.get("spdxId") ?: lics[k]?.get("name") ?: k).toString())
                fun hasText(k: String) = !(lics[k]?.get("content") as? String).isNullOrBlank()
                val own = keys.filter { hasText(it) }.mapNotNull { fam(it) }.toSet()
                val kept = keys.filterNot { !hasText(it) && fam(it) in own }
                if (kept != keys) { lib["licenses"] = kept; changed = true }
            }
            if (changed) f.writeText(groovy.json.JsonOutput.toJson(root))
            (root["libraries"] as? List<Map<String, Any?>>).orEmpty().forEach { lib ->
                val keys = (lib["licenses"] as? List<String>).orEmpty()
                if (keys.isEmpty()) bad += "${lib["uniqueId"]}: no license"
                keys.forEach { k ->
                    val e = lics[k].orEmpty()
                    if ((e["content"] as? String).isNullOrBlank()) {
                        val fam = family((e["spdxId"] ?: e["name"] ?: k).toString())
                        if (fam == null || (fam != "none" && raw.none { fam in it }))
                            bad += "${lib["uniqueId"]}: no text for ${e["name"] ?: k}"
                    }
                }
            }
        }
        if (bad.isNotEmpty()) throw GradleException("Licenses page: add the text in config/licenses or res/raw: " + bad.distinct().joinToString("; "))
    }
}
