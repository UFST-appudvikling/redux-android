package dk.ufst.arch.redux_sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import dk.ufst.arch.redux_sample.android.ui.SampleApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            ReduxApp.init(navController)
            SampleApp(navController, ReduxApp.store)
        }
    }
}

