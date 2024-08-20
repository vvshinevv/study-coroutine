package study.coroutine.chapter12.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StringStateHolder(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {

    private val coroutineScope = CoroutineScope(dispatcher)

    var stringState = ""

    fun updateStringWithDelay(string: String) {
        coroutineScope.launch {
            delay(1000)
            stringState = string
        }
    }
}