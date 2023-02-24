package dk.ufst.arch.redux_sample.android.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dk.ufst.arch.AppAction
import dk.ufst.arch.GlobalStore
import dk.ufst.arch.redux_sample.android.AppEnvironment
import dk.ufst.arch.redux_sample.android.AppState
import dk.ufst.arch.redux_sample.android.NavigationDestination
import dk.ufst.arch.redux_sample.android.ui.theme.SampleTheme
import kotlinx.coroutines.launch

class SampleAppState (
    val scaffoldState: ScaffoldState,
    val navController: NavHostController
)

@Composable
fun rememberSampleAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
) = remember(scaffoldState, navController) {
    SampleAppState(scaffoldState, navController)
}

@Composable
fun SampleApp(appState: SampleAppState, globalStore: GlobalStore<AppState, AppAction, AppEnvironment>) {
    SampleTheme {
        val scope = rememberCoroutineScope()
        Scaffold(
            topBar = { TopAppBar(title = { Text("Android Redux Sample") }) },
            scaffoldState = appState.scaffoldState
        ) { innerPadding ->
            NavHost(
                navController = appState.navController,
                startDestination = NavigationDestination.Contacts.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(
                    route = NavigationDestination.Contacts.route,
                ) {
                    ContactsScreen(
                        globalStore,
                        onShowMessage = { msg ->
                            scope.launch {
                                appState.scaffoldState.snackbarHostState.showSnackbar(msg)
                            }
                        },
                        onGotoMessages = { contactId ->
                            appState.navController.navigate("${NavigationDestination.Messages.route}/$contactId")
                        }
                    )
                }
                composable(
                    route = "${NavigationDestination.Messages.route}/{${CONTACT_ID}}",
                    arguments = listOf(
                        navArgument(CONTACT_ID) {
                            type = NavType.StringType
                        })
                ) { entry ->
                    val contactId = requireNotNull(entry.arguments?.getString(CONTACT_ID))
                    MessagesScreen(globalStore, contactId)
                }
            }
        }
    }
}

const val CONTACT_ID = "contactId"