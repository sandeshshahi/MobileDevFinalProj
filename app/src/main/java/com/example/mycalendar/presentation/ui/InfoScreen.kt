// kotlin
package com.example.mycalendar.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mycalendar.core.database.entity.Note
import com.example.mycalendar.data.local.PreferencesDataSource
import com.example.mycalendar.presentation.viewmodel.NotesViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val prefs = PreferencesDataSource(context)
    val userCredentials by prefs.getUserCredentials().collectAsState(initial = null)
    val username = userCredentials?.username.orEmpty()

    val scope = rememberCoroutineScope()

    val vm: NotesViewModel = viewModel(factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory(context.applicationContext as android.app.Application))
    val notes by vm.userNotes.collectAsState(initial = emptyList())
    var editing by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<Note?>(null) }

    Scaffold(
        topBar =
            {
                TopAppBar(
                    title = { Text("Info / Notes") },
                    actions = {
                        if (username.isNotBlank()) {
                            TextButton(onClick = {
                                scope.launch { prefs.clearUserCredentials() }
                            }) {
                                Text("Logout")
                            }
                        }
                    }
                ) })
    { inner ->
        Column(modifier = Modifier.fillMaxSize().padding(inner).padding(12.dp)) {
            if (username.isBlank()) {

            Text("You are not logged in.", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onNavigateToLogin) { Text("Login") }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onNavigateToRegister) { Text("Register") }
            } else {
                if (notes.isEmpty()) {
                    Text("No notes for this account.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(notes, key = { _, item -> item.id }) { _, note ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { editing = note.id to note.text }
                                    .padding(horizontal = 8.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${note.bsMonth} ${note.bsDate} ${note.enDate}",
                                        modifier = Modifier.padding(bottom = 4.dp),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                    Text(text = note.text)
                                }
                                IconButton(onClick = { showDeleteConfirm = note }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                            Divider()
                        }
                }
            }
            }
        }
    }

//    if (username.isBlank()) {
//        Column(
//            modifier = modifier.fillMaxSize().padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text("You are not logged in.", style = MaterialTheme.typography.titleMedium)
//            Spacer(Modifier.height(12.dp))
//            Button(onClick = onNavigateToLogin) { Text("Login") }
//            Spacer(Modifier.height(8.dp))
//            Button(onClick = onNavigateToRegister) { Text("Register") }
//        }
//    } else {
//        Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
//            Text(text = "Notes for $username", style = MaterialTheme.typography.titleLarge)
//            Spacer(Modifier.height(8.dp))
//            if (notes.isEmpty()) {
//                Text("No notes yet.")
//            } else {
//                LazyColumn {
//                    itemsIndexed(notes) { _, note ->
//                        // Render generically to avoid type coupling
//                        Text(text = note.toString(), style = MaterialTheme.typography.bodyMedium)
//                        Spacer(Modifier.height(8.dp))
//                    }
//                }
//            }
//        }
//    }

    // edit dialog
    editing?.let { (id, current) ->
        var text by remember { mutableStateOf(current) }
        AlertDialog(
            onDismissRequest = { editing = null },
            confirmButton = {
                Button(onClick = {
                    val noteObj = notes.find { it.id == id } ?: return@Button
                    vm.update(noteObj.copy(text = text))
                    editing = null
                }) { Text("Save") }
            },
            dismissButton = { Button(onClick = { editing = null }) { Text("Cancel") } },
            title = { Text("Edit Note") },
            text = { OutlinedTextField(value = text, onValueChange = { text = it }) }
        )
    }

    // delete confirm
    showDeleteConfirm?.let { n ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            confirmButton = {
                Button(onClick = {
                    vm.delete(n)
                    showDeleteConfirm = null
                }) { Text("Delete") }
            },
            dismissButton = { Button(onClick = { showDeleteConfirm = null }) { Text("Cancel") } },
            title = { Text("Delete Note") },
            text = { Text("Delete this note?") }
        )
    }
}
