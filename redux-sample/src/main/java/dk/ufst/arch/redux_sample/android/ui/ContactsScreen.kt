package dk.ufst.arch.redux_sample.android.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dk.ufst.arch.redux_sample.domain.environment.Contact
import dk.ufst.arch.redux_sample.domain.environment.mockData

@Composable
fun ContactsScreen(
    contacts: List<Contact>,
    onItemClick: (contact: Contact)->Unit = {}) {
    ContactList(contacts = contacts, onItemClick = onItemClick)
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
fun ContactsPreview() {
    ContactsScreen(contacts = mockData)
}