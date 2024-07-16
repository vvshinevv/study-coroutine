package study.coroutine.chapter07

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking

import mu.KotlinLogging
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext

class Chapter07Test {

    @Test
    fun chapter07_6(): Unit = runBlocking {
        launch(Dispatchers.IO) {
            val dbResultsDeferred: List<Deferred<String>> = listOf("db1", "db2", "db3").map {
                async {
                    delay(1000L)
                    println("${it}으로부터 데이터를 가져오는데 성공했습니다.")
                    "[${it}]data"
                }
            }
            val dbResults: List<String> = dbResultsDeferred.awaitAll()
            println(dbResults)
        }
    }

    @Test
    fun chapter07_7(): Unit = runBlocking {
        val parentJob = launch(Dispatchers.IO) {
            val dbResultsDeferred: List<Deferred<String>> = listOf("db1", "db2", "db3").map {
                async {
                    delay(1000L)
                    println("${it}으로부터 데이터를 가져오는데 성공했습니다.")
                    "[${it}]data"
                }
            }
            val dbResults: List<String> = dbResultsDeferred.awaitAll()
            println(dbResults)
        }

        parentJob.cancel()
    }

    @Test
    fun chapter07_8(): Unit = runBlocking {
        val startTime = System.currentTimeMillis()
        val parentJob = launch {
            launch {
                delay(1000L)
                println("[${getCurrentTimeMillis(startTime)}] 자식 코루틴 실행 완료")
            }

            println("[${getCurrentTimeMillis(startTime)}] 부모 코루틴이 실행하는 마지막 코드")
        }

        parentJob.invokeOnCompletion {
            println("[${getCurrentTimeMillis(startTime)}] 부모 코루틴 실행 완료")
        }
    }



    @Test
    fun chapter07_11(): Unit = runBlocking {
        val coroutineScope = CustomCoroutineScope()
        coroutineScope.launch {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }
        Thread.sleep(1000L)
    }

    @Test
    fun chapter07_12() {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        coroutineScope.launch {
            delay(100L)
            println("[${Thread.currentThread().name}] 코루틴 실행 완료")
        }

        Thread.sleep(1000L)
    }

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalStdlibApi::class)
    @Test
    fun chapter07_13() {
        val newScope = CoroutineScope(CoroutineName("MyCoroutine") + Dispatchers.IO)
        newScope.launch(CoroutineName("LaunchCoroutine")) {
            println("====> ${this.coroutineContext[CoroutineName]}")
            println("====> ${this.coroutineContext[CoroutineDispatcher]}")

            val launchJob = this.coroutineContext[Job]
            val newScopeJob = newScope.coroutineContext[Job]

            println("=====> launchJob?.parent === newScopeJob >> ${launchJob?.parent === newScopeJob}")
        }

        Thread.sleep(1000L)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun chapter07_14() {
        val newScope = CoroutineScope(CoroutineName("MyCoroutine") + Dispatchers.IO)
        newScope.launch(CoroutineName("LaunchCoroutine")) {
            println(coroutineContext.job.parent)
            this.launch {
                println(coroutineContext[CoroutineName])
                println(coroutineContext.job.parent)
            }
        }

        Thread.sleep(1000L)
    }

    @Test
    fun chapter07_17(): Unit = runBlocking {
        launch(CoroutineName("Coroutine1")) {
            launch(CoroutineName("Coroutine3")) {
                println("[${Thread.currentThread().name}] 코루틴 실행")
            }

            launch(CoroutineName("Coroutine4")) {
                println("[${Thread.currentThread().name}] 코루틴 실행")
            }
        }

        launch(CoroutineName("Coroutine2")) {
            println("[${Thread.currentThread().name}] 코루틴 실행")
        }
    }

    @Test
    fun chapter07_18(): Unit = runBlocking {
        launch(CoroutineName("Coroutine1")) {
            launch(CoroutineName("Coroutine3")) {
                println("[${Thread.currentThread().name}] 코루틴 실행")
            }

            CoroutineScope(Dispatchers.IO).launch(CoroutineName("Coroutine4")) {
                println("[${Thread.currentThread().name}] 코루틴 실행")
            }
        }

        launch(CoroutineName("Coroutine2")) {
            log.info { "[${Thread.currentThread().name}] 코루틴 실행" }
        }
    }

    @Test
    fun chapter07_27(): Unit = runBlocking {
        launch(CoroutineName("Coroutine1")) {
            val newJob = Job()
            launch(CoroutineName("Coroutine2") + newJob) {
                delay(1000L)
                log.info { "[${Thread.currentThread().name} 코루틴 실행]" }
            }

            delay(2000L)
            log.info { newJob }
        }
    }

    @Test
    fun chapter07_28(): Unit = runBlocking {
        launch(CoroutineName("Coroutine1")) {
            val coroutine1Job = this.coroutineContext[Job]
            val newJob = Job(parent = coroutine1Job)
            launch(CoroutineName("Coroutine2") + newJob) {
                delay(1000L)
                log.info { "[${Thread.currentThread().name}] 코루틴 실행" }
            }
        }
    }

    @Test
    fun chapter07_33(): Unit = runBlocking {
        val startTime = System.currentTimeMillis()
        runBlocking {
            delay(1000L)
            log.info { "[${Thread.currentThread().name}] 하위 코루틴 종료" }
        }
        log.info { getCurrentTimeMillis(startTime) }
    }

    @Test
    fun chapter07_34(): Unit = runBlocking {
        val startTime = System.currentTimeMillis()
        launch {
            delay(1000L)
            log.info { "[${Thread.currentThread().name}] 하위 코루틴 종료" }
        }
        log.info { getCurrentTimeMillis(startTime) }
    }

    private fun getCurrentTimeMillis(startTime: Long) = "지난 시간: ${System.currentTimeMillis() - startTime}ms"

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    class CustomCoroutineScope : CoroutineScope {
        override val coroutineContext: CoroutineContext = Job() + newSingleThreadContext("CustomScopeThread")
    }

    companion object {
        private val log = KotlinLogging.logger { }
    }
}