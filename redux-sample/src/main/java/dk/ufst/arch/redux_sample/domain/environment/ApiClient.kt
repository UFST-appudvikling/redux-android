package dk.ufst.arch.redux_sample.domain.environment

interface ApiClient {
    fun getContacts() : Result<List<Contact>>
    fun getMessages(contactId: String) : Result<List<Message>>
}
