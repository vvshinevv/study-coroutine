package study.coroutine.chapter09

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
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

    private fun getCurrentTimeMillis(startTime: Long) = "지난 시간: ${System.currentTimeMillis() - startTime}ms"

    companion object {
        private val log = KotlinLogging.logger { }
    }
}