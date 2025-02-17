package com.javiersc.gradle.plugins.core.test

import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.eclipse.jgit.api.Git
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

val sandboxPath: Path = Paths.get("build/sandbox").apply { toFile().mkdirs() }

val File.sandboxFile: File
    get() = File("$this/build/sandbox")

fun getResource(resource: String): File =
    File(Thread.currentThread().contextClassLoader?.getResource(resource)?.toURI()!!)

infix fun String.copyResourceTo(destination: File) {
    getResource(this).copyRecursively(destination)
}

fun createSandboxFile(prefix: String): File {
    return Files.createTempDirectory(sandboxPath, "$prefix-").toFile()
}

val File.arguments: List<String>
    get() =
        File("$this/ARGUMENTS.txt").readLines().first().split(" ", limit = 3).map { argument ->
            argument.replace("\"", "")
        }

fun testSandbox(
    sandboxPath: String,
    prefix: String = sandboxPath.split("/").last(),
    beforeTest: File.() -> Unit = {},
    taskOutcome: TaskOutcome = TaskOutcome.SUCCESS,
    test: (result: BuildResult, testProjectDir: File) -> Unit,
) {
    val testProjectDir: File = createSandboxFile(prefix)
    sandboxPath copyResourceTo testProjectDir

    beforeTest(testProjectDir)

    GradleRunner.create()
        .withDebug(true)
        .withProjectDir(testProjectDir)
        .withArguments(testProjectDir.arguments)
        .withPluginClasspath()
        .build()
        .run {
            checkArgumentsTasks(testProjectDir, taskOutcome)
            test(this, testProjectDir)
        }
}

fun BuildResult.checkArgumentsTasks(testProjectDir: File, taskOutcome: TaskOutcome) {
    val executedTaskName = ":${testProjectDir.arguments.first()}"
    val task = tasks.first { task -> task.path.endsWith(executedTaskName) }
    task.outcome.shouldBe(taskOutcome)
}

fun File.commitAndCheckout(message: String, branch: String = "sandbox/gradle-plugins") {
    val git: Git = Git.init().setDirectory(this).call()
    git.add().addFilepattern(".").call()
    git.commit().setMessage(message).call()
    git.checkout().setCreateBranch(true).setName(branch).call()
}
