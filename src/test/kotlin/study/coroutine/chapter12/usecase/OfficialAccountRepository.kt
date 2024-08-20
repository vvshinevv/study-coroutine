package study.coroutine.chapter12.usecase

interface OfficialAccountRepository {
    suspend fun searchByName(name: String): Array<Follower.OfficialAccount>
}