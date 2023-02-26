package dk.ufst.arch.redux_sample.domain.messages

import dk.ufst.arch.AppAction
import dk.ufst.arch.reducer
import dk.ufst.arch.redux_sample.domain.environment.ApiClient
import dk.ufst.arch.redux_sample.domain.environment.Message

data class MessagesState(
    var messages: List<Message> = emptyList(),
    var isLoading: Boolean = false,
)

sealed class MessagesAction : AppAction() {
    data class LoadMessages(val contactId: String) : MessagesAction()
    data class MessagesLoaded(val messages: List<Message>): MessagesAction()
    data class OnError(val t: Throwable): MessagesAction()
}

class MessagesEnvironment(
    val apiClient: ApiClient,
)

fun messagesReducer(
    state: MessagesState,
    action: MessagesAction,
    env: MessagesEnvironment
) = reducer<MessagesAction> {
    when(action) {
        is MessagesAction.LoadMessages -> {
            state.isLoading = true
            effect {
                env.apiClient.getMessages(action.contactId).fold(
                    onSuccess = { messages -> MessagesAction.MessagesLoaded(messages) },
                    onFailure = { ex -> MessagesAction.OnError(ex) })
            }
        }
        is MessagesAction.MessagesLoaded -> {
            state.isLoading = false
            state.messages = action.messages
        }
        is MessagesAction.OnError -> TODO()
    }
}
