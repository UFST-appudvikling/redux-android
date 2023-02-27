package dk.ufst.arch.redux_sample.domain.contacts

import dk.ufst.arch.AppAction
import dk.ufst.arch.SingleEvent
import dk.ufst.arch.reducer
import dk.ufst.arch.redux_sample.domain.environment.ApiClient
import dk.ufst.arch.redux_sample.domain.environment.Contact
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

data class ContactsState(
    var contacts: List<Contact> = emptyList(),
    var isLoading: Boolean = false,
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
) = reducer<ContactsAction> {
    when(action) {
        is ContactsAction.LoadContacts -> {
            state.isLoading = true
            effect {
                try {
                    repeat(800) {
                        println("LoadContacts")
                        delay(1000L)
                    }
                } catch (t: CancellationException) {
                    println("LoadContacts coroutine cancelled!!!")
                }
                null
            }
            effect {
                env.apiClient.getContacts().fold(
                    onSuccess = { contacts -> ContactsAction.ContactsLoaded(contacts) },
                    onFailure = { ex -> ContactsAction.OnError(ex) })
            }
        }
        is ContactsAction.ContactsLoaded -> {
            state.isLoading = false
            state.contacts = action.contacts

            effect {
                try {
                    repeat(800) {
                        println("ContactsLoaded")
                        delay(1000L)
                    }
                } catch (t: CancellationException) {
                    println("ContactsLoaded coroutine cancelled!!!")
                }
                null
            }
        }
        is ContactsAction.ContactTapped -> {}
        is ContactsAction.OnError -> {
            // communicate the error back to the UI by updating the state
            action.ex.message?.let {
                state.error = SingleEvent(it)
            }
        }
    }
}
