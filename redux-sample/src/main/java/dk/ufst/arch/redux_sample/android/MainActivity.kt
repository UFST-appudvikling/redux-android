package dk.ufst.arch.redux_sample.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dk.ufst.arch.redux_sample.android.ui.SampleApp
import dk.ufst.arch.redux_sample.android.ui.rememberSampleAppState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val state = rememberSampleAppState()
            ReduxApp.init()
            SampleApp(state, ReduxApp.store)
        }
    }
}

