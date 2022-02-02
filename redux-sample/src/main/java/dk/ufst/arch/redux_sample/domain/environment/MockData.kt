package dk.ufst.arch.redux_sample.domain.environment

data class Message(val text: String = "")
data class Contact(
    val name: String = "",
    val phone: String = "",
    val messages : List<Message> = emptyList()
)


val mockData = listOf(
    Contact(name = "Charlie Testburger", phone = "11111111",
        listOf(
            Message("Hi dude"),
            Message("How are you?"),
            Message("Did you get my text?")
        )
    ),
    Contact(name = "Meinke Dorfheimer", phone = "11111111",
        listOf(Message("Hi dude"))
    ),
    Contact(name = "Torstein Hancock", phone = "11111111",
        listOf(Message("Hi dude"))
    )
)