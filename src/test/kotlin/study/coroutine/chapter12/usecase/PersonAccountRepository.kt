package study.coroutine.chapter12.usecase

interface PersonAccountRepository {
    suspend fun searchByName(name: String): Array<Follower.PersonAccount>
}