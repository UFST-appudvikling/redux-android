package dk.ufst.arch.redux_sample.android.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dk.ufst.arch.*
import dk.ufst.arch.redux_sample.R
import dk.ufst.arch.redux_sample.android.AppEnvironment
import dk.ufst.arch.redux_sample.android.AppState
import dk.ufst.arch.redux_sample.domain.environment.Message
import dk.ufst.arch.redux_sample.domain.environment.mockData
import dk.ufst.arch.redux_sample.domain.messages.MessagesAction
import dk.ufst.arch.redux_sample.domain.messages.MessagesState

@Composable
fun MessagesScreen(globalStore: GlobalStore<AppState, AppAction, AppEnvironment>) {

    Log.e("DEBUG", "Composing MessagesScreen")

    val store: ComposeLocalStore<MessagesState, MessagesAction> = rememberLocalStore(
        globalStore,
        { it.messagesState.copy() },
        { it.messagesState.copy() }
    )

    LaunchedEffect(true) {
        store.send(MessagesAction.Init)
    }

    store.state.value.contact?.messages?.let {
        MessageList(it)
    }
}

@Composable
fun MessageList(messages : List<Message>) {
    LazyColumn {
        items(messages) { msg ->
            MessageCard(msg)
        }
    }
}


@Composable
fun MessageCard(msg: Message) {
    Card(
        modifier = Modifier
            .padding(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp).fillMaxWidth()) {
            Image(
                painter = painterResource(R.drawable.android_logo),
                contentDescription = "Contact Picture",
                modifier = Modifier
                    // Set image size to 40 dp
                    .size(64.dp)
                    // Clip image to be shaped as a circle
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.padding(8.dp)) {
                Text(msg.text, style = MaterialTheme.typography.body1)
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
fun MessageScreenPreview() {
    MessageList(mockData[0].messages)
}