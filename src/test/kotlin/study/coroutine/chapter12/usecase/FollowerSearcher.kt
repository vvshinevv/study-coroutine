package study.coroutine.chapter12.usecase

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class FollowerSearcher(
    private val officialAccountRepository: OfficialAccountRepository,
    private val personAccountRepository: PersonAccountRepository,
) {

    suspend fun searchByName(name: String): List<Follower> = coroutineScope {
        val officialAccountsDeferred = async {
            officialAccountRepository.searchByName(name)
        }

        val personAccountDeferred = async {
            personAccountRepository.searchByName(name)
        }

        return@coroutineScope listOf(
            *officialAccountsDeferred.await(),
            *personAccountDeferred.await()
        )
    }
}