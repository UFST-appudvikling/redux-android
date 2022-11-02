package dk.ufst.arch.redux_sample.domain.environment

private fun simulateLoadTime() {
    val loadTime = 200 + (Math.random() * 1000)
    Thread.sleep(loadTime.toLong())
}

class ApiClientMock : ApiClient {

    override fun getContacts() : Result<List<Contact>> = runCatching {
        //throw RuntimeException("Could not load contacts")
        simulateLoadTime()
        mockContacts
    }

    override fun getMessages(contactId: String): Result<List<Message>> = runCatching {
        simulateLoadTime()
        mockMessages[contactId]!!
    }

}