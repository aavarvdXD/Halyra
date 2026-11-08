import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            @Suppress("DEPRECATION")
            implementation(compose.materialIconsExtended)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.aavarvd.halyra.MainKt"

        nativeDistributions {
            val osName = System.getProperty("os.name").lowercase()
            targetFormats(*when {
                osName.contains("windows") -> arrayOf(TargetFormat.Msi, TargetFormat.Exe)
                osName.contains("linux") -> arrayOf(TargetFormat.Deb, TargetFormat.Rpm)
                osName.contains("mac") -> arrayOf(TargetFormat.Pkg, TargetFormat.Dmg)
                else -> arrayOf(TargetFormat.Deb)
            })
            packageName = "Halyra"
            packageVersion = "0.0.2"
            modules("java.instrument", "jdk.unsupported")
        }
    }
}
