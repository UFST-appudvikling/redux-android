package dk.ufst.arch

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.lang.AssertionError

internal class TestStoreTest : ReduxArchTest() {

    @Before
    fun setup() {
        setupTestStore()
    }

    @Test
    fun `Test that we can run the app reducer through the test store`() {
        testStore.sendAction(Test1Action.TestAction)
        assert(testStore.value.test1State.test)
    }

    @Test
    fun `Test findClass helper extension function`() {
        val actionList = listOf(Test1Action.TestAction, Test2Action.TestAction)
        // test that we can find an element in the list based on its class
        assertTrue(actionList.findClass<Test2Action.TestAction>() == actionList[1])
        assertTrue(actionList.findClass<Test2Action.TestAction>() is Test2Action.TestAction)
        // check that we won't find stuff thats not in the list
        assertNull(actionList.findClass<Test2Action.TestAction2>())
    }

    @Test
    fun `Test that Send steps run reducer`() {
        testStore.run(
            Send(Test1Action.TestAction)
        )
        assert(testStore.value.test1State.test)
    }

    @Test(expected = AssertionError::class)
    fun `Test we cannot Send() with pending actions`() {
        testStore.run(
            Send(Test2Action.TestAction), // this action runs an effect generating a new action
            Send(Test1Action.TestAction)
        )
    }

    @Test
    fun `Test that we can flush pending actions if we don't care about receiving them`() {
        testStore.run(
            Send(Test2Action.TestAction), // this action runs an effect generating a new action
            Flush(),
            Send(Test1Action.TestAction)
        )
    }

    @Test
    fun `Test that receive clear pending actions so that we can send again`() {
        testStore.run(
            Send(Test2Action.TestAction), // this action runs an effect generating a new action
            Receive { _, _ -> },
            Send(Test1Action.TestAction)
        )
    }

    @Test
    fun `Test that receive works`() {
        testStore.run(
            Send(Test2Action.TestAction),
            // verify that we can find the pending action generated and that the effect was run
            Receive { actions, state ->
                assertNotNull(actions.findClass<Test2Action.TestAction2>() != null)
                assertTrue(state.test2State.test)
            }
        )
    }

    @Test
    fun `Test that we can verify against the state after Send`() {
        testStore.run(
            Send(Test2Action.TestAction) { state ->
                assertTrue(state.test2State.test)
            }
        )
    }

    @Test
    fun `Test that if we verify on Send, effects has not run yet, but that after Receive, they have`() {
        testStore.run(
            Send(Test2Action.TestAction) { state ->
                assertFalse(state.test2State.test2)
            },
            Receive { _, state ->
                assertTrue(state.test2State.test2)
            }
        )
    }
}