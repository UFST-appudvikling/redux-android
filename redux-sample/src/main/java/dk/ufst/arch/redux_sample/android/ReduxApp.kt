package dk.ufst.arch.redux_sample.android

import dk.ufst.arch.*
import dk.ufst.arch.redux_sample.domain.contacts.ContactsEnvironment
import dk.ufst.arch.redux_sample.domain.contacts.ContactsState
import dk.ufst.arch.redux_sample.domain.contacts.contactsReducer
import dk.ufst.arch.redux_sample.domain.messages.MessagesEnvironment
import dk.ufst.arch.redux_sample.domain.messages.MessagesState
import dk.ufst.arch.redux_sample.domain.messages.messagesReducer
import dk.ufst.arch.redux_sample.domain.environment.ApiClient
import dk.ufst.arch.redux_sample.domain.environment.ApiClientMock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

data class AppState(
    var contactsState: ContactsState = ContactsState(),
    var messagesState: MessagesState = MessagesState()
)

class AppEnvironment(
    private val apiClient: ApiClient,
    val contacts: ContactsEnvironment = ContactsEnvironment(apiClient),
    val messages: MessagesEnvironment = MessagesEnvironment(apiClient)
)

object ReduxApp {
    lateinit var store : GlobalStore<AppState, AppAction, AppEnvironment>
    private lateinit var environment: AppEnvironment
    private lateinit var state: AppState
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
        createGlobalStore(
            env = env,
            defaultEffectScope = CoroutineScope(Dispatchers.Default),
            reducer = compose(
                appReducer,
            ),
            initialValue = appState,
            copyValue = { state: AppState -> state.copy() }
        )

    // Call from app or activity, pass parameters if different setup is required based values found
    // in shared prefs for instance
    fun init() {
        if(!initialized) {
            ReduxAndroid.init(BuildConfig.DEBUG)
            environment = AppEnvironment(ApiClientMock())
            state = AppState()
            store = setupStore(environment, state)
            initialized = true
        }
    }
}