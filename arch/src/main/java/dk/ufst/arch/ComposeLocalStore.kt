package dk.ufst.arch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember

interface ComposeLocalStore<Value, Action> {
    val state: State<Value>
        @Composable
        get
    fun send(action: Action)
}

@Composable
inline fun <LocalValue, LocalAction, GlobalValue, reified GlobalAction, GlobalEnvironment> rememberLocalStore(
    globalStore: GlobalStore<GlobalValue, GlobalAction, GlobalEnvironment>,
    crossinline getLocalCopy: @DisallowComposableCalls (GlobalValue) -> LocalValue,
    crossinline getInitialValue: @DisallowComposableCalls (GlobalValue) -> LocalValue,
): ComposeLocalStore<LocalValue, LocalAction> {
    val localStore = remember {
        object : ComposeLocalStore<LocalValue, LocalAction> {
            override fun send(action: LocalAction) {
                if (action is GlobalAction) {
                    globalStore.sendAction(action as GlobalAction)
                }
            }

            override val state: State<LocalValue>
                @Composable
                get() = observeAsState()

            @Composable
            private fun observeAsState(): State<LocalValue> {
                var prevLocalValue: LocalValue? = null

                return produceState(getInitialValue(globalStore.value)) {
                    val stateChanger: ((GlobalValue) -> Unit) = { globalValue: GlobalValue ->

                        val newLocalValue = getLocalCopy(globalValue)
                        // Only update value if the local state have changed.
                        if (prevLocalValue != newLocalValue) {
                            if (BuildConfig.DEBUG) {
                                prevLocalValue?.let {
                                    logStateDiff(it, newLocalValue!!)
                                }
                            }
                            value = newLocalValue
                        }
                        prevLocalValue = newLocalValue
                    }

                    globalStore.subscribe(stateChanger)

                    awaitDispose {
                        globalStore.desubscribe(stateChanger)
                    }
                }
            }
        }
    }

    return localStore
}
