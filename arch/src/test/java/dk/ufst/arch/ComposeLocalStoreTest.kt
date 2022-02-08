package dk.ufst.arch

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ComposeLocalStoreTest {
    @get:Rule
    val composeTestRule = createComposeRule()
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
    fun `Test only one subscription per composable no matter how many state accesses`() {
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
            assertEquals(1, globalStore.getSubcriberCount())
        }
    }

    private fun setupStore() {
        globalStore = GlobalStore(
            env = Any(),
            executor = TestExecutor(),
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
                    ) { Any() }
                )
            )
        )
    }

    companion object {
        private data class LocalState(var value: Int = 1)
        private data class AppState(var localState: LocalState = LocalState())

        private val localReducer: ReducerFunc<LocalState, Any, Any> = { localValue, _, _ ->
            localValue.value = 2
            emptyArray()
        }
    }
}
