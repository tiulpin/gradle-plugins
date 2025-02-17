package com.javiersc.gradle.plugins.massive.catalogs.updater

import com.javiersc.kotlin.stdlib.AnsiColor.Foreground.Purple
import com.javiersc.kotlin.stdlib.AnsiColor.Foreground.Red
import com.javiersc.kotlin.stdlib.AnsiColor.Foreground.Yellow
import com.javiersc.kotlin.stdlib.AnsiColor.Reset
import com.javiersc.semver.Version
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

abstract class MassiveCatalogsUpdaterPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tasks.register("updateMassiveCatalogs") { task ->
            task.group = "updater"
            task.description = "Check the latest Massive Catalogs version"

            task.doLast {
                val projects = getAllProjects()
                check(projects.isNotEmpty()) { errorMessage }

                val version =
                    projects.map { project -> getProjectVersion(project) }.distinct().maxOrNull()
                check(version != null) { errorMessage }

                it.project.writeVersionToGradleProperties(version)
            }
        }
    }
}

private val url = "https://repo1.maven.org/maven2/com/javiersc/massive-catalogs"

private val errorMessage = "${Red}There was a problem fetching Massive Catalogs version$Reset"

private fun getAllProjects(): List<String> {
    val links: Elements = Jsoup.connect(url).get().body().select("a[href]")

    return links.mapNotNull { element: Element ->
        if (!element.text().contains("../")) element.text().removeSuffix("/") else null
    }
}

private fun getProjectVersion(project: String): Version {
    val links: Elements = Jsoup.connect("$url/$project").get().body().select("a[href]")
    val version =
        links
            .mapNotNull { element: Element ->
                if (element.text().first().isDigit()) Version(element.text().removeSuffix("/"))
                else null
            }
            .maxByOrNull { it }

    check(version != null) { errorMessage }

    return version
}

private fun Project.writeVersionToGradleProperties(version: Version) {
    logger.lifecycle("${Purple}Latest Massive Catalogs version: $Yellow${version.value}${Reset}")
    val gradleProperties: File = file("${rootProject.rootDir.path}/gradle.properties")

    gradleProperties.apply {
        writeText(
            readLines().joinToString("\n") { line ->
                if (line.startsWith("massiveCatalogs=")) {
                    "massiveCatalogs=${version.value}"
                } else line
            } + "\n"
        )
    }

    file("${rootProject.buildDir}/versions/massive-catalogs.txt").apply {
        ensureParentDirsCreated()
        createNewFile()
        writeText(version.value)
    }
}
