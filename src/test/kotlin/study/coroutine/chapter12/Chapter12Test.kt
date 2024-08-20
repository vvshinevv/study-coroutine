package study.coroutine.chapter12

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import study.coroutine.chapter12.usecase.Follower
import study.coroutine.chapter12.usecase.FollowerSearcher
import study.coroutine.chapter12.usecase.OfficialAccountRepository
import study.coroutine.chapter12.usecase.PersonAccountRepository
import study.coroutine.chapter12.usecase.RepeatAddUseCase
import study.coroutine.chapter12.usecase.StringStateHolder

@OptIn(ExperimentalCoroutinesApi::class)
class Chapter12Test {

    @Test
    fun chapter12_01() = runBlocking {
        // given
        val repeatAddUseCase = RepeatAddUseCase()

        // when
        val result = repeatAddUseCase.add(100)

        // then
        assertEquals(100, result)
    }

    @Test
    fun chapter12_02() {
        val testCoroutineScheduler = TestCoroutineScheduler()
        testCoroutineScheduler.advanceTimeBy(5000L)
        assertEquals(5000L, testCoroutineScheduler.currentTime)
    }

    @Test
    fun chapter12_03() {
        val testCoroutineScheduler = TestCoroutineScheduler()
        val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)

        // given
        var result = 0

        // when
        testCoroutineScope.launch {
            delay(10000L)
            result = 1
            delay(10000L)
            result = 2
            println(Thread.currentThread().name)
        }

        // then
        assertEquals(0, result)

        testCoroutineScheduler.advanceTimeBy(5000L)
        assertEquals(0, result)

        testCoroutineScheduler.advanceTimeBy(6000L)
        assertEquals(1, result)

        testCoroutineScheduler.advanceTimeBy(10000L)
        assertEquals(2, result)
    }

    @Test
    fun chapter12_04() {
        val testCoroutineScheduler = TestCoroutineScheduler()
        val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
        val testCoroutineScope = CoroutineScope(testDispatcher)

        // given
        var result = 0

        // when
        testCoroutineScope.launch {
            delay(10000L)
            result = 1
            delay(10000L)
            result = 2
            println(Thread.currentThread().name)
        }

        // then
        testCoroutineScheduler.advanceUntilIdle()
        assertEquals(2, result)
    }


    @Test
    fun chapter12_05() {
        val testScope = TestScope()

        // given
        var result = 0

        // when
        testScope.launch {
            delay(10_000L)
            result = 1
            delay(10_000L)
            result = 2
        }

        // then
        testScope.advanceUntilIdle()
        assertEquals(2, result)
    }

    @Test
    fun chapter12_06() {
        // given
        var result = 0

        // when
        runTest {
            delay(10_000L)
            result = 1
            delay(10_000L)
            result = 2
        }

        // then
        assertEquals(2, result)
    }

    @Test
    fun chapter12_07() = runTest {
        delay(1000L)
        println("가상 시간: ${this.currentTime}ms")
        delay(1000L)
        println("가상 시간: ${this.currentTime}ms")
    }

    @Test
    fun chapter12_08() = runTest {
        var result = 0
        launch {
            delay(1000L)
            result = 1
        }

        delay(500L)
        println("가상 시간: ${this.currentTime}ms, result = $result")
        println("가상 시간: ${this.currentTime}ms, result = $result")
    }

    @Test
    fun chapter12_09() = runTest {
        var result = 0
        launch {
            delay(1000L)
            result = 1
        }.join()

        println("가상 시간: ${this.currentTime}ms, result = $result")
        advanceUntilIdle()
        println("가상 시간: ${this.currentTime}ms, result = $result")
    }


    private lateinit var followerSearcher: FollowerSearcher

    @BeforeEach
    fun setUp() {
        followerSearcher = FollowerSearcher(
            officialAccountRepository = stubOfficialAccountRepository,
            personAccountRepository = stubPersonAccountRepository
        )
    }

    @Test
    fun chapter12_10() = runTest {
        // given
        val searchName = "A"
        val expectedResults = listOf(companyA, personA)

        // when
        val results = followerSearcher.searchByName(searchName)

        // then
        assertEquals(expectedResults, results)
    }

    @Test
    fun chapter12_11() = runTest {
        // given
        val searchName = "EMPTY"
        val expectedResults = emptyList<Follower>()

        // when
        val results = followerSearcher.searchByName(searchName)

        // then
        assertEquals(expectedResults, results)
    }

    @Test
    fun chapter12_12() = runTest {
        // given
        val stringStateHolder = StringStateHolder()

        // when
        stringStateHolder.updateStringWithDelay("ABC")

        // then
        advanceUntilIdle()
        assertEquals("ABC", stringStateHolder.stringState)
    }

    @Test
    fun chapter12_13() = runTest {
        // given
        val testDispatcher = StandardTestDispatcher()
        val stringStateHolder = StringStateHolder(dispatcher = testDispatcher)

        // when
        stringStateHolder.updateStringWithDelay("ABC")

        // then
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("ABC", stringStateHolder.stringState)
    }

    @Test
    fun chapter12_14() = runTest {
        // given
        var result = 0

        // when
        launch {
            while (true) {
                delay(1000L)
                result += 1
            }
        }

        // then

        advanceTimeBy(1500L)
        assertEquals(1, result)
    }

    @Test
    fun chapter12_15() = runTest {
        // given
        var result = 0

        // when
        launch {
            while (true) {
                delay(1000L)
                result += 1
            }
        }

        // then

        advanceTimeBy(1500L)
        assertEquals(1, result)
    }


    companion object {
        private val companyA = Follower.OfficialAccount(id = "0x0000", name = "CompanyA")
        private val companyB = Follower.OfficialAccount(id = "0x0001", name = "CompanyB")
        private val companyC = Follower.OfficialAccount(id = "0x0002", name = "CompanyC")

        private val stubOfficialAccountRepository = StubOfficialAccountRepository(
            users = listOf(companyA, companyB, companyC)
        )

        private val personA = Follower.PersonAccount(id = "0x1000", name = "personA")
        private val personB = Follower.PersonAccount(id = "0x2000", name = "personB")
        private val personC = Follower.PersonAccount(id = "0x3000", name = "personC")

        private val stubPersonAccountRepository = StubPersonAccountRepository(
            users = listOf(personA, personB, personC)
        )
    }

    class StubOfficialAccountRepository(private val users: List<Follower.OfficialAccount>) : OfficialAccountRepository {
        override suspend fun searchByName(name: String): Array<Follower.OfficialAccount> {
            delay(1000L)
            return users.filter { user -> user.name.contains(name) }.toTypedArray()
        }
    }

    class StubPersonAccountRepository(private val users: List<Follower.PersonAccount>) : PersonAccountRepository {
        override suspend fun searchByName(name: String): Array<Follower.PersonAccount> {
            delay(1000L)
            return users.filter { user -> user.name.contains(name) }.toTypedArray()
        }
    }
}