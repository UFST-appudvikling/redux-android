package dk.ufst.arch.redux_sample.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dk.ufst.arch.redux_sample.Contact
import dk.ufst.arch.redux_sample.R

@Composable
fun ContactList(contacts : List<Contact>, onItemClick: (contact: Contact)->Unit = {}) {
    LazyColumn {
        items(contacts) { contact ->
            ContactCard(contact, onItemClick)
        }
    }
}

@Composable
fun ContactCard(contact: Contact, onItemClick: (contact: Contact)->Unit = {}) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = { onItemClick(contact) })) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
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
                Text(text = contact.name, style = MaterialTheme.typography.subtitle1)
                TabRowDefaults.Divider(thickness = 1.dp, modifier = Modifier.padding(2.dp))
                Text(contact.phone, style = MaterialTheme.typography.body1)
            }
        }
    }
}