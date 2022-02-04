package dk.ufst.arch.redux_sample.domain.environment

private fun simulateLoadTime() {
    val loadTime = 1000 + (Math.random() * 2000)
    Thread.sleep(loadTime.toLong())
}

class ApiClientMock : ApiClient {

    override fun getContacts() : Result<List<Contact>> = runCatching {
        //throw RuntimeException("Could not load contacts")
        simulateLoadTime()
        mockData
    }

}