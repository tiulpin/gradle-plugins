package com.javiersc.gradle.plugins.changelog.integration

import com.javiersc.gradle.plugins.changelog.utils.testChangelog
import com.javiersc.gradle.plugins.core.test.commitAndCheckout
import com.javiersc.gradle.plugins.core.test.testSandbox
import kotlin.test.Test

class AddChangelogItemTest {

    @Test
    fun `added 1`() =
        testSandbox(sandboxPath = "add-changelog-item/sandbox-added-1", test = ::testChangelog)

    @Test
    fun `changed 1`() =
        testSandbox(sandboxPath = "add-changelog-item/sandbox-changed-1", test = ::testChangelog)

    @Test
    fun `deprecated 1`() =
        testSandbox(sandboxPath = "add-changelog-item/sandbox-deprecated-1", test = ::testChangelog)

    @Test
    fun `removed 1`() =
        testSandbox(sandboxPath = "add-changelog-item/sandbox-removed-1", test = ::testChangelog)

    @Test
    fun `fixed 1`() =
        testSandbox(sandboxPath = "add-changelog-item/sandbox-fixed-1", test = ::testChangelog)

    @Test
    fun `updated 1`() =
        testSandbox(sandboxPath = "add-changelog-item/sandbox-updated-1", test = ::testChangelog)

    @Test
    fun `renovate 1`() =
        testSandbox(sandboxPath = "add-changelog-item/sandbox-renovate-1", test = ::testChangelog)

    @Test
    fun `renovate 2`() =
        testSandbox(sandboxPath = "add-changelog-item/sandbox-renovate-2", test = ::testChangelog)

    @Test
    fun `renovate 3`() =
        testSandbox(sandboxPath = "add-changelog-item/sandbox-renovate-3", test = ::testChangelog)

    @Test
    fun `renovate 4`() =
        testSandbox(
            sandboxPath = "add-changelog-item/sandbox-renovate-4",
            beforeTest = {
                commitAndCheckout(
                    message =
                        """
                            | datasource | package                                                   | from  | to  |
                            | ---------- | --------------------------------------------------------- | ----- | --- |
                            | maven      | com.gradle.enterprise:com.gradle.enterprise.gradle.plugin | 3.6.4 | 3.7 |
                        """.trimIndent(),
                )
            },
            test = ::testChangelog,
        )

    @Test
    fun `renovate 5`() =
        testSandbox(
            sandboxPath = "add-changelog-item/sandbox-renovate-5",
            beforeTest = {
                commitAndCheckout(
                    message =
                        """
                            | datasource | package                                                   | from  | to    |
                            | ---------- | --------------------------------------------------------- | ----- | ----- |
                            | maven      | com.gradle.enterprise:com.gradle.enterprise.gradle.plugin | 3.6.4 | 3.7   |
                            | maven      | com.javiersc.mokoki:mokoki-core                           | 1.0.0 | 1.0.1 |
                            | maven      | com.javiersc.either:either-core                           | 2.0.1 | 2.0.2 |
                            | maven      | com.javiersc.either:either-core                           | 2.0.1 | 2.0.2 |
                        """.trimIndent(),
                )
            },
            test = ::testChangelog,
        )

    @Test
    fun `renovate 6`() =
        testSandbox(
            sandboxPath = "add-changelog-item/sandbox-renovate-6",
            beforeTest = {
                commitAndCheckout(
                    message =
                        """
                            | datasource | package                                                   | from  | to    |
                            | ---------- | --------------------------------------------------------- | ----- | ----- |
                            | maven      | com.javiersc.mokoki:mokoki-core                           | 1.0.0 | 1.0.1 |
                        """.trimIndent(),
                )
            },
            test = ::testChangelog,
        )

    @Test
    fun `renovate 7`() =
        testSandbox(
            sandboxPath = "add-changelog-item/sandbox-renovate-7",
            beforeTest = {
                commitAndCheckout(
                    message =
                        """
                            | datasource  | package       | from   | to     |
                            | ----------- | ------------- | ------ | ------ |
                            | github-tags | actions/cache | v2.1.6 | v2.1.7 |
                            | github-tags | actions/cache | v2.1.6 | v2.1.7 |
                            | github-tags | actions/cache | v2.1.6 | v2.1.7 |
                            | github-tags | actions/cache | v2.1.6 | v2.1.7 |
                            | github-tags | actions/cache | v2.1.6 | v2.1.7 |
                            | github-tags | actions/cache | v2.1.6 | v2.1.7 |
                        """.trimIndent(),
                )
            },
            test = ::testChangelog,
        )
}
