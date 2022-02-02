package dk.ufst.arch.redux_sample.domain.contacts

import android.util.Log
import dk.ufst.arch.AppAction
import dk.ufst.arch.Effect
import dk.ufst.arch.SingleEvent
import dk.ufst.arch.redux_sample.domain.environment.*
import dk.ufst.arch.singleEffect

data class ContactsState(
    var contacts: List<Contact> = emptyList(),
    var error: SingleEvent<String>? = null
)

sealed class ContactsAction : AppAction() {
    object LoadContacts : ContactsAction()
    data class OnError(val ex: Throwable) : ContactsAction()
    data class ContactsLoaded(val contacts: List<Contact>) : ContactsAction()
    data class ContactTapped(val contact: Contact) : ContactsAction()
}

class ContactsEnvironment(
    val apiClient: ApiClient,
    val navigationClient: NavigationClient
)

/**
 * This reducer is handling the business logic for the contacts screen
 * Reducers are not supposed to have side effects themselves which is why
 * the reducer returns/emit sideeffects instead, which in turn generates new actions
 */
fun contactsReducer(
    state: ContactsState,
    action: ContactsAction,
    env: ContactsEnvironment
): Array<Effect<ContactsAction>> {
    when(action) {
        is ContactsAction.LoadContacts -> {
            Log.e("DEBUG", "Running LoadContacts")
            if(state.contacts.isEmpty()) {
                Log.e("DEBUG", "Actually loading contacts")
                return singleEffect {
                    env.apiClient.getContacts().fold(
                        onSuccess = { contacts -> ContactsAction.ContactsLoaded(contacts) },
                        onFailure = { ex -> ContactsAction.OnError(ex) })
                }
            }
        }
        is ContactsAction.ContactsLoaded -> {
            state.contacts = action.contacts
        }
        is ContactsAction.ContactTapped -> {
            return env.navigationClient.navigateFx(NavigationDestination.Messages, NavigationArg.MessagesArg(action.contact))
        }
        is ContactsAction.OnError -> {
            // communicate the error back to the UI by updating the state
            action.ex.message?.let {
                state.error = SingleEvent(it)
            }
        }
    }
    return emptyArray()
}