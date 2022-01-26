package dk.ufst.arch.redux_sample.domain.messages

import dk.ufst.arch.AppAction
import dk.ufst.arch.Effect
import dk.ufst.arch.redux_sample.Message

data class MessagesState(
    var messages : List<Message> = emptyList()
)

sealed class MessagesAction : AppAction() {
    object Back : MessagesAction()
}

class MessagesEnvironment

fun messagesReducer(
    state: MessagesState,
    action: MessagesAction,
    env: MessagesEnvironment
): Array<Effect<MessagesAction>> {
    
    return emptyArray()
}
