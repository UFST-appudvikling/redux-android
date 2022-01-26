package dk.ufst.arch.redux_sample.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dk.ufst.arch.AppAction
import dk.ufst.arch.ComposeLocalStore
import dk.ufst.arch.GlobalStore
import dk.ufst.arch.redux_sample.Contact
import dk.ufst.arch.redux_sample.domain.AppEnvironment
import dk.ufst.arch.redux_sample.domain.AppState
import dk.ufst.arch.redux_sample.domain.contacts.ContactsAction
import dk.ufst.arch.redux_sample.domain.contacts.ContactsState
import dk.ufst.arch.redux_sample.domain.messages.MessagesAction
import dk.ufst.arch.redux_sample.domain.messages.MessagesState
import dk.ufst.arch.redux_sample.mockData
import dk.ufst.arch.redux_sample.ui.theme.SampleTheme
import dk.ufst.arch.rememberLocalStore

@Composable
fun SampleApp(globalStore: GlobalStore<AppState, AppAction, AppEnvironment>) {
    SampleTheme {
        val navController = rememberNavController()
        var selectedContact by remember { mutableStateOf<Contact?>(null) }

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
                startDestination = Screens.Contacts.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screens.Contacts.name) {
                    ContactsScreen(contacts = mockData, onItemClick = { contact ->
                        selectedContact = contact
                        navController.navigate(Screens.Messages.name)
                    })
                }
                composable(Screens.Messages.name) {
                    MessagesScreen(selectedContact!!)
                }
            }
        }
    }

}


/*
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "Light Mode"
)

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun DefaultPreview() {
    SampleApp()
}
 */