package dk.ufst.arch

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

internal class LocalStoreTest : ReduxArchTest() {

    private lateinit var localStore: LocalStore<Test1State, Test1Action>

    @Before
    fun setup() {
        setupStore()
        localStore = createLocalStore(
            store
        ) { it.test1State.copy() }
    }

    @Test
    fun `Test that we can create a local store and receive updates from it`() {
        var subscriberHasRun = false
        localStore.subscribe {
            subscriberHasRun = true
        }
        localStore.send(Test1Action.TestAction)
        assertTrue(subscriberHasRun)
    }

    @Test
    fun `Test that we can create a local store, subscribe and desubscribe from it`() {
        var subscriberHasRun = false
        val subscriber = { _ : Test1State ->
            subscriberHasRun = true
        }
        localStore.subscribe(subscriber)
        localStore.desubscribe(subscriber)
        localStore.send(Test1Action.TestAction)
        assertFalse(subscriberHasRun)
    }

    @Test
    fun `Test that we can create a local store and update the global store through it`() {
        localStore.send(Test1Action.TestAction)
        // check that update issued through the local store is reflected in the global store
        assertTrue(store.value.test1State.test)
    }

    @Test
    fun `Test that updates to the state are correctly reflected in the subscriber update`() {
        // This test assume we are getting the update (which is tested elsewhere)
        localStore.subscribe { localState ->
            assertTrue(localState.test)
        }
        localStore.send(Test1Action.TestAction)
    }

    @Test(expected = IllegalStateException::class)
    fun `Test that we cannot subscribe twice to a local store`() {
        // This test assume we are getting the update (which is tested elsewhere)
        localStore.subscribe {}
        localStore.subscribe {}
        localStore.send(Test1Action.TestAction)
    }

    @Test
    fun `Test that we only get a subscriber update if the state has actually changed`() {
        var count = 0
        localStore.subscribe {
            count++
        }
        // first action updates Test1State.test from false to true
        localStore.send(Test1Action.TestAction)
        // second action just set it to true again, leaving the state unchanged, so we shouldn't
        // get a subscriber callback
        localStore.send(Test1Action.TestAction)
        // count should be 1 since we've only been notified once
        assertEquals(1, count)
    }
}