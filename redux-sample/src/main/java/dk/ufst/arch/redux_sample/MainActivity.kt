package dk.ufst.arch.redux_sample

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dk.ufst.arch.redux_sample.ui.ContactsScreen
import dk.ufst.arch.redux_sample.ui.MessagesScreen
import dk.ufst.arch.redux_sample.ui.Screens
import dk.ufst.arch.redux_sample.ui.theme.SampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleApp()
        }
    }
}

@Composable fun SampleApp() {
    SampleTheme {
        val navController = rememberNavController()
        var selectedContact by remember { mutableStateOf<Contact?>(null) }
        //var selectedContact by rememberSaveable { mutableStateOf<Contact?>(null)}
        Scaffold(
            topBar = { TopAppBar(title = { Text("Android Redux Sample") }) },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screens.Contacts.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screens.Contacts.name) {
                    ContactsScreen(contacts = mockData, onItemClick = { contact ->
                        selectedContact = contact
                        navController.navigate(Screens.Messages.name)
                    })
                }
                composable(Screens.Messages.name) {
                    MessagesScreen(selectedContact!!)
                }
            }
        }
    }
}



@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    name = "Light Mode"
)

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun DefaultPreview() {
    SampleApp()
}