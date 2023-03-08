package dk.ufst.arch.redux_sample.domain.environment

import kotlinx.coroutines.delay

private suspend fun simulateLoadTime() {
    val loadTime = 200 + (Math.random() * 1000)
    delay(loadTime.toLong())
}

class ApiClientMock : ApiClient {

    override suspend fun getContacts() : Result<List<Contact>> = runCatching {
        //throw RuntimeException("Could not load contacts")
        simulateLoadTime()
        mockContacts
    }

    override suspend fun getMessages(contactId: String): Result<List<Message>> = runCatching {
        simulateLoadTime()
        mockMessages[contactId]!!
    }

}
