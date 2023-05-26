package dk.ufst.arch

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule

/**
 * App state with 2 substates, 2 reducers and a highorder reducer for testing
 */
internal data class AppState(
    var test1State: Test1State = Test1State(),
    var test2State: Test2State = Test2State(),
    var test: Boolean = false
)

internal class AppEnvironment {
    var test1Environment : Test1Environment = Test1Environment()
    var test2Environment : Test2Environment = Test2Environment()
}

internal data class Test1State(
    var test: Boolean = false
)

internal sealed class Test1Action : AppAction() {
    object TestAction : Test1Action()
}

internal class Test1Environment

@Suppress("UNUSED_PARAMETER")
internal fun reducer1(
    state: Test1State,
    action: Test1Action,
    env: Test1Environment
) = reducer<Test1Action> {
    when(action) {
        is Test1Action.TestAction -> state.test = true
    }
}

internal data class Test2State(
    var test: Boolean = false,
    var test2: Boolean = false
)

internal sealed class Test2Action : AppAction() {
    object TestAction : Test2Action()
    object TestAction2 : Test2Action()
}

internal class Test2Environment

@Suppress("UNUSED_PARAMETER")
internal fun reducer2(
    state: Test2State,
    action: Test2Action,
    env: Test2Environment
) = reducer<Test2Action> {
    when(action) {
        is Test2Action.TestAction -> {
            state.test = true
            effect {
                Test2Action.TestAction2
            }
        }
        Test2Action.TestAction2 -> {
            state.test2 = true
        }
    }
}

/**
 * High order reducer which "listens" for Test1Action.TestAction and modifies the global
 * state.
 */
internal fun highOrderReducer(reducer: ReducerFunc<AppState, AppAction, AppEnvironment>)
        : ReducerFunc<AppState, AppAction, AppEnvironment> {
    return { state, action, env ->
        val effects = reducer(state, action, env).toMutableList()

        when (action) {
            is Test1Action.TestAction -> {
                state.test = true
                //effects.add { Test2Action.TestAction }
            }

        }
        effects.toTypedArray()
    }
}

/**
 * Convenient base class to cut down on the copy pasting. Has function for setting up a regular
 * GlobalStore or a TestStore and an AppReducer suitable for testing
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("RemoveExplicitTypeArguments") // suppressed because it will not compile if you remove the types
internal abstract class ReduxArchTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    val testScope = CoroutineScope(mainDispatcherRule.testDispatcher)

    private val appReducer = combine(
        pullback<AppState, Test1State, AppAction, Test1Action, AppEnvironment, Test1Environment>(::reducer1,
            AppState::test1State::get,
            AppState::test1State::set,
            AppEnvironment::test1Environment::get
        ),
        pullback<AppState, Test2State, AppAction, Test2Action, AppEnvironment, Test2Environment>(::reducer2,
            AppState::test2State::get,
            AppState::test2State::set,
            AppEnvironment::test2Environment::get
        ),
    )

    protected lateinit var store : GlobalStore<AppState, AppAction, AppEnvironment>
    protected lateinit var testStore : TestStore<AppState, AppAction, AppEnvironment>

    fun setupStore() {
        val state = AppState()
        store = createGlobalStore(
            env = AppEnvironment(),
            defaultEffectScope = testScope,
            reducer = compose(appReducer, ::highOrderReducer),
            initialValue = state,
            copyValue = { state.copy() },
        )
    }

    fun setupTestStore() {
        val state = AppState()
        testStore = TestStore(
            env = AppEnvironment(),
            reducer = compose(appReducer, ::highOrderReducer),
            initialValue = state,
            copyValue = { state.copy() },
            dispatcher = mainDispatcherRule.testDispatcher,
        )
    }
}