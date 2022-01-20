package dk.ufst.arch.redux_sample.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import dk.ufst.arch.redux_sample.Contact

enum class Screens {
    Contacts, Messages
}

@Composable fun ContactsScreen(contacts : List<Contact>, onItemClick: (contact: Contact)->Unit = {}) {
    ContactList(contacts = contacts, onItemClick = onItemClick)
}

@Composable fun MessagesScreen(contact: Contact) {
    Text(contact.name)
}