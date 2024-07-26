package study.coroutine.chapter08

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import mu.KotlinLogging
import org.junit.jupiter.api.Test

class Chapter08Test {

    @Test
    fun chapter08_6(): Unit = runBlocking {
        val coroutineScope = CoroutineScope(SupervisorJob())
        coroutineScope.apply {
            launch(CoroutineName("coroutine1")) {
                launch(CoroutineName("coroutine3")) {
                    throw Exception("예외발생")
                }
                delay(1000L)
                log.info { "[${Thread.currentThread().name}] 코루틴 실행" }
            }
        }

        launch(CoroutineName("coroutine2")) {
            delay(1000L)
            log.info { "[${Thread.currentThread().name}] 코루틴 실행" }
        }
    }

    @Test
    fun chapter08_7(): Unit = runBlocking {
        launch(CoroutineName("Parent Coroutine") + SupervisorJob()) {
            launch(CoroutineName("Coroutine1")) {
                launch(CoroutineName("Coroutine3")) {
                    throw Exception("예외발생")
                }

                delay(1000L)
                log.info { "[${Thread.currentThread().name}] 코루틴 실행" }
            }
            launch(CoroutineName("coroutine2")) {
                delay(1000L)
                log.info { "[${Thread.currentThread().name}] 코루틴 실행" }
            }
        }
        delay(1000L)
    }

    @Test
    fun chapter08_09(): Unit = runBlocking {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            log.info { "[예외 발생] $throwable" }
        }

        CoroutineScope(exceptionHandler).launch(CoroutineName("Coroutine1")) {
            throw Exception("Coroutine1에 예외가 발생.")
        }
    }

    @Test
    fun chapter08_10(): Unit = runBlocking {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            log.info { "[예외 발생] $throwable" }
        }

        launch(CoroutineName("Coroutine1") + exceptionHandler) {
            throw Exception("Coroutine1에 예외가 발생.")
        }
    }

    @Test
    fun chapter08_11(): Unit = runBlocking {
        val coroutineContext = Job() + CoroutineExceptionHandler { _, throwable ->
            log.info { "[예외 발생] $throwable" }
        }

        launch(CoroutineName("Coroutine1") + coroutineContext) {
            throw Exception("Coroutine1에 예외가 발생.")
        }

        delay(1000L)
    }

    @Test
    fun chapter08_12(): Unit = runBlocking {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            log.info { "[예외 발생] $throwable" }
        }
        val supervisedScope = CoroutineScope(SupervisorJob() + exceptionHandler)
        supervisedScope.apply {
            launch(CoroutineName("Coroutine1")) {
                throw Exception("Coroutine1에 예외가 발생.")
            }
            launch(CoroutineName("Coroutine2")) {
                delay(1000L)
                log.info { "[${Thread.currentThread().name}] 코루틴 실행" }
            }
        }

        delay(2000L)
    }

    @Test
    fun chapter08_13(): Unit = runBlocking {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            log.info { "[예외 발생] $throwable" }
        }

        launch(CoroutineName("Coroutine1") + exceptionHandler) {
            launch(CoroutineName("Coroutine2")) {
                throw Exception("예외발생")
            }
        }

        delay(1000L)
        log.info { "runBlocking coroutine" }
    }

    @Test
    fun chapter08_14(): Unit = runBlocking {
        launch(CoroutineName("Coroutine1")) {
            try {
                throw Exception("Coroutine1에 예외가 발생.")
            } catch (e: Exception) {
                log.info { e.message }
            }
        }

        launch(CoroutineName("Coroutine2")) {
            delay(1000L)
            log.info { "Coroutine2 실행 완료" }
        }
    }

    @Test
    fun chapter08_15(): Unit = runBlocking {
        try {
            launch(CoroutineName("Coroutine1")) {
                throw Exception("Coroutine1에 예외가 발생.")
            }
        } catch (e: Exception) {
            log.info { e.message }
        }

        launch(CoroutineName("Coroutine2")) {
            delay(1000L)
            log.info { "Coroutine2 실행 완료" }
        }
    }

    @Test
    fun chapter08_16(): Unit = runBlocking {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            log.info { "[예외 발생1] $throwable" }
        }

        val deferred: Deferred<String> = async(CoroutineName("Coroutine1") + exceptionHandler) {
            throw Exception("Coroutine1에 예외가 발생.")
        }

        try {
            delay(1000L)
            deferred.await()
        } catch (e: Exception) {
            log.info { "[예외 발생2] ${e.message}" }
        }
    }

    @Test
    fun chapter08_17(): Unit = runBlocking {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            log.info { "[예외 발생1] $throwable" }
        }
        val supervisedScope = CoroutineScope(SupervisorJob())
        supervisedScope.apply {
            async(CoroutineName("Coroutine1")) {
                throw Exception("Coroutine1에 예외가 발생.")
            }

            launch(CoroutineName("Coroutine2")) {
                delay(1000L)
                log.info { "[${Thread.currentThread().name}] 코루틴 실행" }
            }
        }
    }

    @Test
    fun chapter08_18(): Unit = runBlocking {

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            log.info { "[예외 발생1] $throwable" }
        }

        launch(CoroutineName("Parent") + exceptionHandler) {
            launch(CoroutineName("Coroutine1")) {
                throw Exception("Coroutine1에 예외가 발생.")
            }

            launch(CoroutineName("Coroutine2")) {
                delay(1000L)
                log.info { "[${Thread.currentThread().name}] 코루틴 실행" }
            }
        }

        delay(1000L)
    }

    @Test
    fun chapter08_19(): Unit = runBlocking {

        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            log.info { "[예외 발생1] $throwable" }
        }

        launch(CoroutineName("Parent") + exceptionHandler) {
            launch(CoroutineName("Coroutine1")) {
                throw Exception("Coroutine1 예외가 발생.")
            }
        }
    }

    @Test
    fun chapter08_20(): Unit = runBlocking {
        val coroutineContext = Job() + CoroutineExceptionHandler { _, throwable ->
            log.info { "[예외 발생] $throwable" }
        }

        val deferred = async(CoroutineName("Coroutine1") + coroutineContext) {
            throw Exception("Coroutine1에 예외가 발생.")
        }

        deferred.await()
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}