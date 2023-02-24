package dk.ufst.arch.redux_sample.android

sealed class NavigationDestination(
    val route: String,
) {
    object Contacts: NavigationDestination(
        route = "contacts",
    )
    object Messages: NavigationDestination(
        route = "messages",
    )
}
