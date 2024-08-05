package study.coroutine.chapter09

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import mu.KotlinLogging
import org.junit.jupiter.api.Test

class Chapter09Test {

    @Test
    fun chapter09_1(): Unit = runBlocking {
        delayAndPrintHelloWorld()
        delay(2000L)
    }

    private suspend fun delayAndPrintHelloWorld() {
        delay(1000L)
        log.info { "Hello World" }
    }

    @Test
    fun chapter09_12(): Unit = runBlocking {
        val startTime = System.currentTimeMillis()
        val result = searchByKeyword("hongs")
        log.info { result }
        log.info { getCurrentTimeMillis(startTime) }
    }

    suspend fun searchByKeyword(keyword: String): List<String> = supervisorScope {
        val dbResultsDeferred = async {
            searchFromDB(keyword)
        }

        val serverResultsDeferred = async {
            searchFromServer(keyword)
        }

        dbResultsDeferred.await() + serverResultsDeferred.await()
    }

    private suspend fun searchFromDB(keyword: String): List<String> {
        delay(1000L)
        return arrayListOf("[DB] ${keyword}_1", "[DB] ${keyword}_2")
    }

    private suspend fun searchFromServer(keyword: String): List<String> {
        delay(1000L)
        return arrayListOf("[SV] ${keyword}_1", "[SV] ${keyword}_2")
    }

    @Test
    fun chapter09_ex1(): Unit = runBlocking {
        val startTime = System.currentTimeMillis()
        val notifications = listOf("aa", "bb", "cc")
//        log.info { "runBlocking:: 본인 - ${coroutineContext[Job]}" }
        sendNotificationNormal(this, notifications)
        log.info { getCurrentTimeMillis(startTime) }
    }

    private suspend fun sendNotificationNormal(notificationScope: CoroutineScope, notifications: List<String>) {
        for (notification: String in notifications) {
            notificationScope.launch {
                delay(1000L)
//                log.info { "notificationScope.launch:: 부모 - ${coroutineContext[Job]?.parent} / 본인 - ${coroutineContext[Job]}" }
                log.info { "[전송성공 (normal)] $notification" }
            }
        }
    }

    @Test
    fun chapter09_ex2(): Unit = runBlocking {
        val startTime = System.currentTimeMillis()
        val notifications = listOf("aa", "bb", "cc")
//        log.info { "runBlocking:: 본인 - ${coroutineContext[Job]}" }
        sendNotificationSuspend(notifications)
        log.info { getCurrentTimeMillis(startTime) }
    }

    private suspend fun sendNotificationSuspend(notifications: List<String>) = coroutineScope {
//        log.info { "notificationScope.launch:: 부모 - ${coroutineContext[Job]?.parent} / 본인 - ${coroutineContext[Job]}" }
        for (notification: String in notifications) {
            launch {
                delay(1000L)
                log.info { "[전송성공 (suspend)] $notification" }
            }
        }
    }

    @Test
    fun chapter09_ex3(): Unit = runBlocking {
        val startTime = System.currentTimeMillis()
        val notifications = listOf("aa", "bb", "cc")
        try {
            log.info { "runBlocking:: 본인 - ${coroutineContext[Job]}" }
            sendNotificationNormalThrow(this, notifications)
        } catch (e: RuntimeException) {
            log.error { "[예외발생] ${e.message}" }
        }
        log.info { getCurrentTimeMillis(startTime) }
    }

    private fun sendNotificationNormalThrow(notificationScope: CoroutineScope, notifications: List<String>) {
        for (notification: String in notifications) {
            notificationScope.launch {
                delay(1000L)
                log.info { "notificationScope.launch:: 부모 - ${coroutineContext[Job]?.parent} / 본인 - ${coroutineContext[Job]}" }
                if (notification == "bb") throw RuntimeException("예외발생")
                log.info { "[전송성공 (normal)] $notification" }
            }
        }
    }

    @Test
    fun chapter09_ex4(): Unit = runBlocking {
        val startTime = System.currentTimeMillis()
        val notifications = listOf("aa", "bb", "cc")
        try {
            log.info { "runBlocking:: 본인 - ${coroutineContext[Job]}" }
            sendNotificationSuspendThrow(notifications)
        } catch (e: RuntimeException) {
            log.error { "[예외발생] ${e.message}" }
        }
        log.info { getCurrentTimeMillis(startTime) }
    }

    private suspend fun sendNotificationSuspendThrow(notifications: List<String>) = coroutineScope {
        log.info { "coroutineScope.launch:: 부모 - ${coroutineContext[Job]?.parent} / 본인 - ${coroutineContext[Job]}" }
        for (notification: String in notifications) {
            launch {
                delay(1000L)
                log.info { "launch:: 부모 - ${coroutineContext[Job]?.parent} / 본인 - ${coroutineContext[Job]}" }
                if (notification == "bb") throw RuntimeException("예외가 발생했습니다.")
                log.info { "[전송성공 (suspend)] $notification" }
            }
        }
    }


    @Test
    fun chapter09_ex6(): Unit = runBlocking {
        val startTime = System.currentTimeMillis()
        val notifications = listOf("aa", "bb", "cc")
        try {
            log.info { "runBlocking:: 본인 - ${coroutineContext[Job]}" }
            sendNotificationThrow1(notifications)
        } catch (e: RuntimeException) {
            log.error { "[예외발생] ${e.message}" }
        }
        log.info { getCurrentTimeMillis(startTime) }
    }

    private suspend fun sendNotificationThrow1(notifications: List<String>) {
        coroutineScope {
            log.info { "coroutineScope.launch:: 부모 - ${coroutineContext[Job]?.parent} / 본인 - ${coroutineContext[Job]}" }
            for (notification: String in notifications) {
                launch {
                    delay(1000L)
                    log.info { "launch:: 부모 - ${coroutineContext[Job]?.parent} / 본인 - ${coroutineContext[Job]}" }
                    if (notification == "bb") throw RuntimeException("예외가 발생했습니다.")
                    log.info { "[전송성공 (suspend)] $notification" }
                }
            }
        }
    }

    @Test
    fun chapter09_ex7(): Unit = runBlocking {
        val startTime = System.currentTimeMillis()
        val notifications = listOf("aa", "bb", "cc")
        try {
            log.info { "runBlocking:: 본인 - ${coroutineContext[Job]}" }
            sendNotificationThrow(notifications)
        } catch (e: RuntimeException) {
            log.error { "[예외발생] ${e.message}" }
        }
        log.info { getCurrentTimeMillis(startTime) }
    }

    private fun CoroutineScope.sendNotificationThrow(notifications: List<String>) {
        log.info { "coroutineScope.launch:: 부모 - ${coroutineContext[Job]?.parent} / 본인 - ${coroutineContext[Job]}" }
        for (notification: String in notifications) {
            launch {
                try {
                    delay(1000L)
                    log.info { "launch:: 부모 - ${coroutineContext[Job]?.parent} / 본인 - ${coroutineContext[Job]}" }
                    if (notification == "bb") throw RuntimeException("예외가 발생했습니다.")
                    log.info { "[전송성공 (suspend)] $notification" }
                } catch (e: RuntimeException) {
                    log.error { "[launch 예외 발생] ${e.message}" }
                    throw e
                }
            }
        }
    }


    @Test
    fun test1() = runBlocking {
        val parentJob = launch {
            try {
                coroutineScope {
                    delay(1000)
                    throw RuntimeException("Exception in some function")
                }

            } catch (e: Exception) {
                log.info { "Caught exception: ${e.message}" }
            }
        }
        parentJob.join()
    }


    @Test
    fun test2() = runBlocking {
        val parentJob = launch {
            try {
                someSuspendFunction()
            } catch (e: Exception) {
                log.info { "Caught exception: ${e.message}" }
            }
        }

        parentJob.join()
    }

    private suspend fun someSuspendFunction(): Nothing = coroutineScope {
        delay(1000)
        throw RuntimeException("Exception in suspend function")
    }

    private fun getCurrentTimeMillis(startTime: Long) = "지난 시간: ${System.currentTimeMillis() - startTime}ms"

    companion object {
        private val log = KotlinLogging.logger { }
    }
}