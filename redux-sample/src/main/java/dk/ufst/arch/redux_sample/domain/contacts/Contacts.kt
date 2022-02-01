package dk.ufst.arch.redux_sample.domain.contacts

import dk.ufst.arch.AppAction
import dk.ufst.arch.Effect
import dk.ufst.arch.redux_sample.Contact
import dk.ufst.arch.redux_sample.mockData

data class ContactsState(
    var contacts: List<Contact> = mockData
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
    when(action) {
        is ContactsAction.ContactTapped -> {
            state.contacts = state.contacts.plus(Contact("VongDurf", "222222", emptyList()))
        }
    }
    return emptyArray()
}