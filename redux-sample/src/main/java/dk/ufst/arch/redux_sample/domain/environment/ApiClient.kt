package dk.ufst.arch.redux_sample.domain.environment

interface ApiClient {
    suspend fun getContacts() : Result<List<Contact>>
    suspend fun getMessages(contactId: String) : Result<List<Message>>
}
