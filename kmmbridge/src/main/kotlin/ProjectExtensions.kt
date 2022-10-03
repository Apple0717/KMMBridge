package co.touchlab.faktory

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.provideDelegate
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import java.io.File
import kotlin.reflect.KProperty

internal val Project.kotlin: KotlinMultiplatformExtension get() = extensions.getByType()
internal val Project.kmmBridgeExtension get() = extensions.getByType<KmmBridgeExtension>()

// Cocoapods is an extension of KMP extension so you can't just do project.extensions.getByType<CocoapodsExtension>()
internal val KotlinMultiplatformExtension.cocoapods get() = (this as ExtensionAware).extensions.findByType<CocoapodsExtension>()
    ?: error("You must apply the org.jetbrains.kotlin.native.cocoapods plugin to use cocoapods() configuration")

internal val Project.urlFile get() = file("$buildDir/faktory/url")


internal fun Project.zipFilePath(): File {
    val tempDir = file("$buildDir/faktory/zip")
    val artifactName = "frameworkarchive.zip"
    return file("$tempDir/$artifactName")
}

// This is a little hacky so we get something that works like `by lazy {}` but lets us access `this`.
internal val Project.kmmBridgeVersion by object {
    lateinit var finalVersion: String

    operator fun getValue(thisRef: Project, property: KProperty<*>): String {
        if (!::finalVersion.isInitialized) {
            finalVersion = thisRef.kmmBridgeExtension.versionManager.get().getVersion(thisRef,  thisRef.kmmBridgeExtension.versionPrefix.get())
        }
        return finalVersion
    }
}

internal fun Project.findStringProperty(name: String): String? {
    rootProject.extensions.getByType(ExtraPropertiesExtension::class.java).run {
        if (has(name))
            return get(name).toString()
    }
    return null
}
