package study.coroutine.chapter01

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class Chapter01Test {

    @Test
    fun chapter01_1() = runBlocking {
        println("${Thread.currentThread().name} 첫번째 코루틴 테스트")
    }
}