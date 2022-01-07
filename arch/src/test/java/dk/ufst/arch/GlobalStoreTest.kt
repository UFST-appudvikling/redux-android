package dk.ufst.arch

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

internal class GlobalStoreTest : ReduxArchTest() {

    @Before
    fun setup() {
        setupStore()
    }

    @Test
    fun `Test reducer combine() and pullback() function`() {
        // send an action to each reducer
        store.sendAction(Test1Action.TestAction)
        store.sendAction(Test2Action.TestAction)
        // verify that the two reducers have run and updated
        // each of their local environments, which is then reflected
        // in the global environment
        assertTrue(store.value.test1State.test)
        assertTrue(store.value.test2State.test)
    }

    @Test
    fun `Test compose() function`() {
        store.sendAction(Test1Action.TestAction)
        assertTrue(store.value.test)
    }

    @Test
    fun `Test singleEffect helper`() {
        // Run the simple effect helper with takes a function that takes no arguments and returns
        // an action and converts it to an Array of functions that return an action.
        // bit dumb since we are testing arrayOf() :D
        val actions = singleEffect<Test2Action> { Test2Action.TestAction }
        // verify its the same action
        assertTrue(actions.any { it.invoke() is Test2Action.TestAction })
    }

    @Test
    fun `Test that effects are run`() {
        // TestAction runs an effect which emit Test2Action.TestAction2 which in turn updates
        // Test2State.test2
        store.sendAction(Test2Action.TestAction)
        assertTrue(store.value.test2State.test2)
    }

    @Test
    fun `Test subscribers are notified of changes in the global state`() {
        var subscriberHasRun = false
        store.subscribe { state ->
            subscriberHasRun = true
            assertTrue(state.test1State.test)
        }
        store.sendAction(Test1Action.TestAction)
        assertTrue(subscriberHasRun)
    }

    @Test
    fun `Test that we can have more than one subscriber`() {
        var subscriber1HasRun = false
        var subscriber2HasRun = false
        store.subscribe {
            subscriber1HasRun = true
        }
        store.subscribe {
            subscriber2HasRun = true
        }
        store.sendAction(Test1Action.TestAction)
        assertTrue(subscriber1HasRun)
        assertTrue(subscriber2HasRun)
    }

    @Test
    fun `Test that we can desubscribe from global state updates`() {
        var count = 0
        val subscriber = { _ : AppState ->
            count++
            Unit
        }
        // first we subscribe and send an action
        store.subscribe(subscriber)
        store.sendAction(Test1Action.TestAction)
        // verify subscriber has run
        assertEquals(1, count)
        // desubscribe and send action
        store.desubscribe(subscriber)
        store.sendAction(Test1Action.TestAction)
        // count should still be 1 one since we desubcribed
        assertEquals(1, count)
    }
}