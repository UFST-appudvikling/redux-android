package dk.ufst.arch

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeLocalStoreTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testScope = CoroutineScope(Dispatchers.Main)
    private lateinit var globalStore: GlobalStore<AppState, Any, Any>

    @Before
    fun setup() {
        setupStore()
    }

    @Test
    fun `Test correct recomposition and State data are observed`() {
        val composedResults = mutableListOf<LocalState>()
        // Signal an action sent from the Compose scope to the reducer.
        val signalAction = Channel<Unit>(1)

        composeTestRule.setContent {
            val localStore: ComposeLocalStore<LocalState, Any> = rememberLocalStore(
                globalStore
            ) { it.localState.copy() }

            composedResults.add(localStore.state.value)

            val scope = rememberCoroutineScope()

            // Wait for outside signal for when to sent an Action.
            scope.launch {
                signalAction.consumeEach {
                    localStore.send(Any())
                }
            }
        }

        composeTestRule.runOnIdle {
            assertEquals(composedResults.single().value, 1)
            signalAction.trySend(Unit)
        }

        composeTestRule.runOnIdle {
            assertEquals(composedResults.size, 2)
            assertEquals(composedResults.first().value, 1)
            assertEquals(composedResults.last().value, 2)
        }
    }

    @Test
    fun `Test no recomposition when forgetting to use copied initial and local values`() {
        val composedResults = mutableListOf<LocalState>()
        // Signal an action sent from the Compose scope to the reducer.
        val signalAction = Channel<Unit>(1)

        composeTestRule.setContent {
            val localStore: ComposeLocalStore<LocalState, Any> = rememberLocalStore(
                globalStore,
            ) { it.localState }

            composedResults.add(localStore.state.value)

            val scope = rememberCoroutineScope()

            // Wait for outside signal for when to sent an Action.
            scope.launch {
                signalAction.consumeEach {
                    localStore.send(Any())
                }
            }
        }

        composeTestRule.runOnIdle {
            assertEquals(composedResults.single().value, 1)
            signalAction.trySend(Unit)
        }

        composeTestRule.runOnIdle {
            assertEquals(composedResults.single().value, 2)
        }
    }

    @Test
    fun `Test only one subscription per ComposeLocalStore no matter how many state accesses`() {
        // Signal an action sent from the Compose scope to the reducer.
        val signalAction = Channel<Unit>(1)

        composeTestRule.setContent {
            val localStore: ComposeLocalStore<LocalState, Any> = rememberLocalStore(
                globalStore
            ) { it.localState.copy() }

            // Access the state 2 times
            localStore.state.value
            localStore.state.value

            val scope = rememberCoroutineScope()

            // Wait for outside signal for when to sent an Action.
            scope.launch {
                signalAction.consumeEach {
                    localStore.send(Any())
                }
            }
        }

        composeTestRule.runOnIdle {
            signalAction.trySend(Unit)
        }

        composeTestRule.runOnIdle {
            // Make sure that only one subscription is created
            assertEquals(1, globalStore.subscriberCount)
        }
    }

    @Test
    fun `Test that effects are cancelled when leaving composition`() {
        effectCancelled = false
        // Signal an action sent from the Compose scope to the reducer.
        val signalAction = Channel<Unit>(1)

        val state = mutableStateOf(0)

        composeTestRule.setContent {
            println("Compositing")
            if(state.value == 0) {
                val localStore: ComposeLocalStore<LocalState, Any> = rememberLocalStore(
                    globalStore
                ) { it.localState.copy() }

                val scope = rememberCoroutineScope()
                // Wait for outside signal for when to sent an Action.
                scope.launch {
                    signalAction.consumeEach {
                        localStore.send(Any())
                        println("sending action")
                    }
                }
            }
        }
        composeTestRule.runOnIdle {
            signalAction.trySend(Unit)
            state.value = 1
        }

        composeTestRule.runOnIdle {
            println("effectCancelled: $effectCancelled")
            assertTrue(effectCancelled)
        }

    }

    private fun setupStore() {
        globalStore = createGlobalStore(
            env = Any(),
            defaultEffectScope = testScope,
            initialValue = AppState(),
            copyValue = { appState ->
                appState.copy()
            },
            reducer = compose(
                combine(
                    pullback(
                        localReducer,
                        AppState::localState::get,
                        AppState::localState::set
                    ) { Any() },
                    pullback(
                        ::localReducer2,
                        AppState::localState::get,
                        AppState::localState::set
                    ) { Any() }
                )
            )
        )
    }

    companion object {
        private data class LocalState(var value: Int = 1)
        private data class AppState(var localState: LocalState = LocalState())

        var effectCancelled: Boolean = false

        private fun localReducer2(state: LocalState, action: Any, env: Any ) = reducer<Any> {
            effect {
                try {
                    repeat(800) {
                        println("coroutine running")
                        delay(1000L)
                    }
                } catch (t: CancellationException) {
                    effectCancelled = true
                    println("coroutine cancelled!!!")
                }
                null
            }
        }

        private val localReducer: ReducerFunc<LocalState, Any, Any> = { localValue, _, _ ->
            localValue.value = 2
            emptyArray()
        }
    }
}
