package dk.ufst.arch.redux_sample.domain.environment

class ApiClientMock : ApiClient {

    override fun getContacts() : Result<List<Contact>> = runCatching {
        //throw RuntimeException("Could not load contacts")
        mockData
    }

}