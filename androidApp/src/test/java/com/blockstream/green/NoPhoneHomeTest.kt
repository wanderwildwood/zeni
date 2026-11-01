package com.blockstream.green

import org.junit.Test
import kotlin.test.assertFailsWith

/** The analytics and help-desk SDKs are gone from the build, so no request to them can be made. */
class NoPhoneHomeTest {
    private val gone = listOf(
        "ly.count.android.sdk.Countly",
        "zendesk.core.Zendesk",
        "zendesk.support.Support",
        "com.android.installreferrer.api.InstallReferrerClient",
        "com.blockstream.green.data.Countly",
    )

    @Test
    fun sdkClassesAreNotOnTheClasspath() {
        gone.forEach { name ->
            assertFailsWith<ClassNotFoundException>(name) { Class.forName(name) }
        }
    }
}
