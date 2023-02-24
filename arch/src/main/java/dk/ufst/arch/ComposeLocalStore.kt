package dk.ufst.arch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


interface ComposeLocalStore<Value, Action> {
    val state: State<Value>
    fun send(action: Action)
}

@Composable
inline fun <LocalValue, LocalAction, GlobalValue, reified GlobalAction, GlobalEnvironment> rememberLocalStore(
    globalStore: GlobalStore<GlobalValue, GlobalAction, GlobalEnvironment>,
    crossinline getLocalCopy: @DisallowComposableCalls (GlobalValue) -> LocalValue
): ComposeLocalStore<LocalValue, LocalAction> {
    var prevLocalValue: LocalValue = remember { getLocalCopy(globalStore.value) }

    val state = produceState(getLocalCopy(globalStore.value)) {
        val stateChanger: ((GlobalValue) -> Unit) = { globalValue: GlobalValue ->

            val newLocalValue = getLocalCopy(globalValue)
            // Only update value if the local state have changed.
            if (prevLocalValue != newLocalValue) {
                logStateDiff(prevLocalValue!!, newLocalValue!!)
                value = newLocalValue
            }
            prevLocalValue = newLocalValue
        }

        globalStore.subscribe(stateChanger)

        awaitDispose {
            globalStore.desubscribe(stateChanger)
        }
    }

    val localStore = remember {
        object : ComposeLocalStore<LocalValue, LocalAction> {
            override fun send(action: LocalAction) {
                if (action is GlobalAction) {
                    globalStore.sendAction(action as GlobalAction, scope)
                }
            }
            private val scope = CoroutineScope(Dispatchers.Default)
            override val state: State<LocalValue> = state
        }
    }

    return localStore
}
