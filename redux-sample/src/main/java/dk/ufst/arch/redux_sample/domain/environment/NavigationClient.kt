package dk.ufst.arch.redux_sample.domain.environment

enum class NavigationDestination {
    Contacts,
    Messages
}

sealed class NavigationArg {
    data class MessagesArg(val contact: Contact) : NavigationArg()
}

interface NavigationClient {
    fun navigate(dst: NavigationDestination, arg: NavigationArg? = null)
    fun pop()
    fun getArgument(): NavigationArg?
}