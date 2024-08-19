package study.coroutine.chapter11

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread
import kotlin.coroutines.resume

class Chapter11Test {

    @Test
    fun chapter11_1(): Unit = runBlocking {
        val job = launch {
            log.info { "작업0" }
            delay(1000L)
            log.info { "작업1" }

        }
        delay(1000L)
        log.info { "작업2" }
        job.cancel()
    }

    @Test
    fun chapter11_25(): Unit = runBlocking {
        val result = suspendCancellableCoroutine { continuation ->
            thread {
                Thread.sleep(2000L)
                continuation.resume("실행 결과")
            }
        }
        log.info { result }
    }

    private var count = 0
    suspend fun increase() = coroutineScope {
        withContext(newSingleThreadContext("context")) {
            count += 1
        }
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}