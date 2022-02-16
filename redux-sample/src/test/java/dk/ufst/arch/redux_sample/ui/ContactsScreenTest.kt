package dk.ufst.arch.redux_sample.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dk.ufst.arch.redux_sample.android.ui.theme.SampleTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VehicleListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun vehicleListContent_registrableVehicle() {
        composeTestRule.setContent {
            SampleTheme {
                //VehicleListContent(vehicleCardList = MockData.vehicleList.map { it.toVehicleCardView() }) {}
            }
        }

        composeTestRule.onNode(
            matcher = hasText("AA12345") and
                hasAnySibling(hasText("Mercedes Sprinter 314 CDI L2 H1"))
        ).assertExists().assertIsDisplayed()

        composeTestRule.onNode(
            matcher = hasText("XX12345") and
                hasAnySibling(hasText("Kan ikke ejerskifte dette køretøj")),
            useUnmergedTree = true
        ).assertExists().assertIsDisplayed()

        composeTestRule.onNode(
            matcher = hasText("BB12345") and
                hasAnySibling(hasText("Mercedes Sprinter 314 CDI L2 H1"))
        ).assertExists()
    }

    /*
    @Test
    fun vehicleListContent_emptyList() {
        composeTestRule.setContent {
            EjerskifteTheme {
                VehicleListContent(vehicleListState = TestVehicleData.emptyVehicleListState) {}
            }
        }
        composeTestRule.onNodeWithText("Du har ingen køretøjer").assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun vehicleListContent_error() {
        composeTestRule.setContent {
            EjerskifteTheme {
                VehicleListContent(vehicleListState = TestVehicleData.errorVehicleListState) {}
            }
        }
        composeTestRule.onNodeWithText(TestVehicleData.errorVehicleListState.message)
            .assertExists().assertIsDisplayed()
    }

     */
}
