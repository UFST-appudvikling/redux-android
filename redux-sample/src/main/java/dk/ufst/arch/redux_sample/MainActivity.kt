package dk.ufst.arch.redux_sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dk.ufst.arch.redux_sample.domain.Redux
import dk.ufst.arch.redux_sample.ui.SampleApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Redux.init()
        setContent {
            SampleApp()
        }
    }
}

