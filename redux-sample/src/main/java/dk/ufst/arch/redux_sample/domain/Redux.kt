package dk.ufst.arch.redux_sample.domain

import dk.ufst.arch.*
import dk.ufst.arch.redux_sample.domain.contacts.ContactsEnvironment
import dk.ufst.arch.redux_sample.domain.contacts.ContactsState
import dk.ufst.arch.redux_sample.domain.contacts.contactsReducer
import dk.ufst.arch.redux_sample.domain.messages.MessagesEnvironment
import dk.ufst.arch.redux_sample.domain.messages.MessagesState
import dk.ufst.arch.redux_sample.domain.messages.messagesReducer


data class AppState(
    var contactsState: ContactsState = ContactsState(),
    var messagesState: MessagesState = MessagesState()
)

class AppEnvironment(
    var contacts: ContactsEnvironment = ContactsEnvironment(),
    var messages: MessagesEnvironment = MessagesEnvironment()
)

object Redux {
    lateinit var appStore : GlobalStore<AppState, AppAction, AppEnvironment>
    lateinit var appEnvironment: AppEnvironment
    lateinit var appState: AppState
    private var initialized = false

    private val appReducer = combine<AppState, AppAction, AppEnvironment>(
        pullback(
            ::contactsReducer,
            AppState::contactsState::get,
            AppState::contactsState::set,
            AppEnvironment::contacts::get
        ),
        pullback(
            ::messagesReducer,
            AppState::messagesState::get,
            AppState::messagesState::set,
            AppEnvironment::messages::get
        )
    )

    private fun setupStore(env: AppEnvironment, appState: AppState) =
        GlobalStore(
            env = env,
            executor = ThreadExecutor(),
            reducer = compose(
                appReducer
            ),
            initialValue = appState,
            copyValue = { state: AppState -> state.copy() }
        )

    // Call from app or activity, pass parameters if different setup is required based values found
    // in shared prefs for instance
    fun init() {
        if(!initialized) {
            appEnvironment = AppEnvironment()
            appState = AppState()
            appStore = setupStore(appEnvironment, appState)
            initialized = true
        }
    }
}