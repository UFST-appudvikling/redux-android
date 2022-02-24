package dk.ufst.arch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.State
import androidx.compose.runtime.remember


import androidx.compose.runtime.*

/**
 * Implement RememberObserver interface which keeps us notified of when
 * were remembered or forgotten in the composition.
 *
 * This ensures that we subscribe once and get one update from the GlobalStore
 * even though the state is accessed several times in a composable
 */
interface ComposeLocalStore<Value, Action> : RememberObserver {
    val state: State<Value>
        @Composable
        get
    fun send(action: Action)
}

@Composable
inline fun <LocalValue, LocalAction, GlobalValue, reified GlobalAction, GlobalEnvironment> rememberLocalStore(
    globalStore: GlobalStore<GlobalValue, GlobalAction, GlobalEnvironment>,
    crossinline getLocalCopy: @DisallowComposableCalls (GlobalValue) -> LocalValue,
): ComposeLocalStore<LocalValue, LocalAction> {
    val localStore = remember {
        object : ComposeLocalStore<LocalValue, LocalAction> {
            override fun send(action: LocalAction) {
                if (action is GlobalAction) {
                    globalStore.sendAction(action as GlobalAction)
                }
            }

            private var mutableState = mutableStateOf(getLocalCopy(globalStore.value))
            private var prevLocalValue: LocalValue = getLocalCopy(globalStore.value)

            private val stateChange: ((GlobalValue) -> Unit) = { globalValue: GlobalValue ->
                val newLocalValue = getLocalCopy(globalValue)
                // Only update value if the local state have changed.
                if (prevLocalValue != newLocalValue) {
                    if (BuildConfig.DEBUG) {
                        logStateDiff(prevLocalValue!!, newLocalValue!!)
                    }
                    mutableState.value = newLocalValue
                }
                prevLocalValue = newLocalValue
            }

            override val state: State<LocalValue>
                @Composable
                get() = mutableState

            override fun onAbandoned() {
                globalStore.desubscribe(stateChange)
            }

            override fun onForgotten() {
                globalStore.desubscribe(stateChange)
            }

            override fun onRemembered() {
                globalStore.subscribe(stateChange)
            }
        }
    }

    return localStore
}
