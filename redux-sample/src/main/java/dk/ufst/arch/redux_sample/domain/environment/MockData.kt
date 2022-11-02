package dk.ufst.arch.redux_sample.domain.environment

data class Message(val text: String = "")

data class Contact(
    val id: String,
    val name: String = "",
    val phone: String = "",
)

val mockContacts = listOf(
    Contact(id = "1", name = "Charlie Testburger", phone = "11111111"),
    Contact(id = "2", name = "Meinke Dorfheimer", phone = "11111112"),
    Contact(id = "3", name = "Torstein Hancock", phone = "11111113")
)

val mockMessages = mapOf<String, List<Message>>(
    mockContacts[0].id to listOf(
        Message("message text 1"),
        Message("message text 2"),
        Message("message text 3"),
        Message("message text 4"),
    ),
    mockContacts[1].id to listOf(
        Message("message text 5"),
        Message("message text 6"),
        Message("message text 7"),
    ),
    mockContacts[2].id to listOf(
        Message("message text 8"),
        Message("message text 9"),
    )
)