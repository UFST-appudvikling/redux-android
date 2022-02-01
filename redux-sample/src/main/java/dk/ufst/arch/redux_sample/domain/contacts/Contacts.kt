package dk.ufst.arch.redux_sample.domain.contacts

import dk.ufst.arch.AppAction
import dk.ufst.arch.Effect
import dk.ufst.arch.redux_sample.domain.environment.*

data class ContactsState(
    var contacts: List<Contact> = mockData
)

sealed class ContactsAction : AppAction() {
    data class ContactTapped(val contact: Contact) : ContactsAction()
}

class ContactsEnvironment(
    val apiClient: ApiClient,
    val navigationClient: NavigationClient
)

fun contactsReducer(
    state: ContactsState,
    action: ContactsAction,
    env: ContactsEnvironment
): Array<Effect<ContactsAction>> {
    when(action) {
        is ContactsAction.ContactTapped -> {
            env.navigationClient.navigate(NavigationDestination.Messages)
            //state.contacts = state.contacts.plus(Contact("VongDurf", "222222", emptyList()))
        }
    }
    return emptyArray()
}