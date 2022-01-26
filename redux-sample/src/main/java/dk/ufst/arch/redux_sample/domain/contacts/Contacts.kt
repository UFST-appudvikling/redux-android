package dk.ufst.arch.redux_sample.domain.contacts

import dk.ufst.arch.AppAction
import dk.ufst.arch.Effect
import dk.ufst.arch.redux_sample.Contact

data class ContactsState(
    var contacts: List<Contact> = emptyList()
)

sealed class ContactsAction : AppAction() {
    data class ContactTapped(val contact: Contact) : ContactsAction()
}

class ContactsEnvironment

fun contactsReducer(
    state: ContactsState,
    action: ContactsAction,
    env: ContactsEnvironment
): Array<Effect<ContactsAction>> {

    return emptyArray()
}