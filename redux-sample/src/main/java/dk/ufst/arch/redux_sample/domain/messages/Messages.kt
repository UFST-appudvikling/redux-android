package dk.ufst.arch.redux_sample.domain.messages

import dk.ufst.arch.AppAction
import dk.ufst.arch.Effect
import dk.ufst.arch.redux_sample.domain.environment.*

data class MessagesState(
    var contact: Contact? = null,
)

sealed class MessagesAction : AppAction() {
    object Init : MessagesAction()
    object Back : MessagesAction()
}

class MessagesEnvironment(
    val apiClient: ApiClient,
    val navigationClient: NavigationClient
)

fun messagesReducer(
    state: MessagesState,
    action: MessagesAction,
    env: MessagesEnvironment
): Array<Effect<MessagesAction>> {
    when(action) {
        MessagesAction.Back -> {}
        MessagesAction.Init -> {
            Thread.sleep(3000L)
            env.navigationClient.getArgument(NavigationDestination.Messages.name)?.let {
                if(it is NavigationArg.MessagesArg) {
                    state.contact = it.contact
                }
            }
        }
    }
    return emptyArray()
}
