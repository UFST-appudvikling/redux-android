package dk.ufst.arch.redux_sample.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dk.ufst.arch.AppAction
import dk.ufst.arch.ComposeLocalStore
import dk.ufst.arch.GlobalStore
import dk.ufst.arch.redux_sample.android.AppEnvironment
import dk.ufst.arch.redux_sample.android.AppState
import dk.ufst.arch.redux_sample.domain.contacts.ContactsAction
import dk.ufst.arch.redux_sample.domain.contacts.ContactsState
import dk.ufst.arch.redux_sample.domain.messages.MessagesAction
import dk.ufst.arch.redux_sample.domain.messages.MessagesState
import dk.ufst.arch.redux_sample.domain.environment.mockData
import dk.ufst.arch.redux_sample.android.ui.theme.SampleTheme
import dk.ufst.arch.redux_sample.domain.environment.NavigationDestination
import dk.ufst.arch.rememberLocalStore

@Composable
fun SampleApp(navController: NavHostController, globalStore: GlobalStore<AppState, AppAction, AppEnvironment>) {
    SampleTheme {
        val contactsStore: ComposeLocalStore<ContactsState, ContactsAction> = rememberLocalStore(
            globalStore,
            { it.contactsState.copy() },
            { it.contactsState.copy() }
        )

        val messagesStore: ComposeLocalStore<MessagesState, MessagesAction> = rememberLocalStore(
            globalStore,
            { it.messagesState.copy() },
            { it.messagesState.copy() }
        )

        Scaffold(
            topBar = { TopAppBar(title = { Text("Android Redux Sample") }) },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = NavigationDestination.Contacts.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(NavigationDestination.Contacts.name) {
                    ContactsScreen(
                        contacts = contactsStore.state.value.contacts,
                        onInit = {
                            contactsStore.send(ContactsAction.Init)
                        },
                        onItemClick = { contact ->
                            contactsStore.send(ContactsAction.ContactTapped(contact))
                        }
                    )
                }
                composable(NavigationDestination.Messages.name) {

                    MessagesScreen(messagesStore.state.value.contact!!)
                }
            }
        }
    }
}