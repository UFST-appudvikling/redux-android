package dk.ufst.arch.redux_sample.domain.messages

import dk.ufst.arch.AppAction
import dk.ufst.arch.Effects
import dk.ufst.arch.ReducerFunc
import dk.ufst.arch.reducer
import dk.ufst.arch.redux_sample.domain.environment.ApiClient
import dk.ufst.arch.redux_sample.domain.environment.Message
import dk.ufst.arch.subReducer

data class MessagesState(
    var messages: List<Message> = emptyList(),
    var isLoading: Boolean = false,
)

class MessagesEnvironment(
    val apiClient: ApiClient,
)

/**
 * Map actions to their subreducers
 */
sealed class MessagesAction(val reducer: ReducerFunc<MessagesState, MessagesAction, MessagesEnvironment>) : AppAction() {
    data class LoadMessages(val contactId: String) : MessagesAction(subReducer(::loadMessagesReducer))
    data class MessagesLoaded(val messages: List<Message>): MessagesAction(subReducer(::messagesLoadedReducer))
    data class OnError(val t: Throwable): MessagesAction(subReducer(::onErrorReducer))
}

private fun messagesLoadedReducer(
    state: MessagesState,
    action: MessagesAction.MessagesLoaded,
    env: MessagesEnvironment
) = reducer<MessagesAction> {
    state.isLoading = false
    state.messages = action.messages
}

private fun loadMessagesReducer(
    state: MessagesState,
    action: MessagesAction.LoadMessages,
    env: MessagesEnvironment
) = reducer<MessagesAction> {
    state.isLoading = true
    effect {
        env.apiClient.getMessages(action.contactId).fold(
            onSuccess = { messages -> MessagesAction.MessagesLoaded(messages) },
            onFailure = { ex -> MessagesAction.OnError(ex) })
    }
}

private fun onErrorReducer(
    state: MessagesState,
    action: MessagesAction.OnError,
    env: MessagesEnvironment
) = reducer<MessagesAction> {

}

fun messagesReducer(
    state: MessagesState,
    action: MessagesAction,
    env: MessagesEnvironment
): Effects<MessagesAction> = action.reducer.invoke(state, action, env)
