package dk.ufst.arch.redux_sample.domain.environment

import dk.ufst.arch.Effect

enum class NavigationDestination {
    Contacts,
    Messages
}

sealed class NavigationArg {
    data class MessagesArg(val contact: Contact) : NavigationArg()
}

interface NavigationClient {
    fun navigate(dst: NavigationDestination, arg: NavigationArg? = null)
    fun <Action>navigateFx(dst : NavigationDestination, arg: NavigationArg? = null): Array<Effect<Action>>
    fun pop()
    fun <Action> popFx(): Array<Effect<Action>>
    fun getArgument(key: String): NavigationArg?
}