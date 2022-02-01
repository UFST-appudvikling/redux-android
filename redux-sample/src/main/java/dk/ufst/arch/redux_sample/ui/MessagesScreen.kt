package dk.ufst.arch.redux_sample.ui

import android.content.res.Configuration
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dk.ufst.arch.redux_sample.Contact
import dk.ufst.arch.redux_sample.mockData

@Composable
fun MessagesScreen(contact: Contact) {
    Text(contact.name)
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
fun MessagesPreview() {
    MessagesScreen(contact = mockData[0])
}