package dk.ufst.arch.redux_sample.android.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.ufst.arch.*
import dk.ufst.arch.redux_sample.R
import dk.ufst.arch.redux_sample.android.AppEnvironment
import dk.ufst.arch.redux_sample.android.AppState
import dk.ufst.arch.redux_sample.domain.contacts.ContactsAction
import dk.ufst.arch.redux_sample.domain.contacts.ContactsState
import dk.ufst.arch.redux_sample.domain.environment.Contact
import dk.ufst.arch.redux_sample.domain.environment.mockContacts

@Composable
fun ContactsScreen(
    globalStore: GlobalStore<AppState, AppAction, AppEnvironment>,
    onGotoMessages: (String)->Unit,
    onShowMessage: (String)->Unit = {},
) {

    val store: ComposeLocalStore<ContactsState, ContactsAction> = rememberLocalStore(
        globalStore,
    ) { it.contactsState.copy() }

    LaunchedEffect(true) {
        store.send(ContactsAction.LoadContacts)
    }

    store.state.value.error.consume {
        onShowMessage(it)
    }

    if(store.state.value.isLoading) {
        LoadingIndicator()
    } else {
        ContactList(
            contacts = store.state.value.contacts,
            onItemClick = { contact ->
                onGotoMessages(contact.id)
            }
        )
    }
}

@Composable
fun ContactList(contacts : List<Contact>, onItemClick: (contact: Contact)->Unit = {}) {
    LazyColumn {
        items(contacts) { contact ->
            ContactCard(contact, onItemClick)
        }
    }
}

@Composable
fun ContactCard(contact: Contact, onItemClick: (contact: Contact)->Unit = {}) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = { onItemClick(contact) })) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(R.drawable.android_logo),
                contentDescription = "Contact Picture",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = contact.name, style = MaterialTheme.typography.subtitle1)
                TabRowDefaults.Divider(thickness = 1.dp, modifier = Modifier.padding(2.dp))
                Text(contact.phone, style = MaterialTheme.typography.body1)
            }
        }
    }
}

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
fun ContactsPreview() {
    ContactList(contacts = mockContacts)
}