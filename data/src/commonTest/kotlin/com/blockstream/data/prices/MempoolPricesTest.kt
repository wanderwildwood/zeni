package com.blockstream.data.prices

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull

class MempoolPricesTest {

    @BeforeTest
    @AfterTest
    fun reset() = MempoolPrices.reset()

    @Test
    fun an_answer_is_used_and_kept() = runTest {
        // In real time: the request runs on another dispatcher, and the test clock would
        // otherwise skip the wait for it at once.
        withContext(Dispatchers.Default) { answerIsUsedAndKept() }
    }

    private suspend fun answerIsUsedAndKept() {
        val via: suspend (String) -> JsonElement = {
            buildJsonObject { putJsonObject("body") { put("USD", 50_000) } }
        }
        assertEquals(50_000.0, MempoolPrices.rate("usd", via))
        // Fresh for five minutes: no second request.
        assertEquals(50_000.0, MempoolPrices.rate("USD") { error("asked again") })
    }

    @Test
    fun a_request_that_never_answers_is_not_waited_for_past_the_timeout() = runTest {
        // The test clock skips the ten seconds. Like a first request over Tor that waits for
        // GDK to connect, which no cancellation reaches.
        val never = CompletableDeferred<JsonElement>()
        assertNull(MempoolPrices.rate("USD") { never.await() })

        // For the next minute no one waits again, and no second request is made.
        var askedAgain = false
        assertNull(MempoolPrices.rate("USD") { askedAgain = true; never.await() })
        assertFalse(askedAgain)
        never.cancel()
    }
}
