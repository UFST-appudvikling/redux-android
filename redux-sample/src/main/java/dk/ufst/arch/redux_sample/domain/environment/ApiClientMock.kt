package dk.ufst.arch.redux_sample.domain.environment

class ApiClientMock : ApiClient {

    override fun getContacts() : Result<List<Contact>> = runCatching {
        mockData
    }

    override fun getMessages(contactId: Int) : Result<List<Message>> = runCatching {
        mockData[0].messages
    }
}