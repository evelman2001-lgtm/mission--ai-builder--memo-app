package com.macnotes.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.macnotes.model.Note
import com.macnotes.storage.NoteStorage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    var notes by remember { mutableStateOf(NoteStorage.loadAll()) }
    var selectedNote by remember { mutableStateOf(notes.firstOrNull()) }
    var saveJob by remember { mutableStateOf<Job?>(null) }

    fun saveDebounced(updatedNote: Note) {
        saveJob?.cancel()
        saveJob = scope.launch {
            delay(400)
            notes = NoteStorage.updateNote(updatedNote)
            if (selectedNote?.id == updatedNote.id) {
                selectedNote = updatedNote
            }
        }
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxSize()) {
                NotesList(
                    notes = notes,
                    selectedId = selectedNote?.id,
                    onSelect = { selectedNote = it },
                    onCreate = {
                        val newNote = NoteStorage.createNote()
                        notes = NoteStorage.loadAll()
                        selectedNote = newNote
                    },
                    onDelete = { id ->
                        notes = NoteStorage.deleteNote(id)
                        if (selectedNote?.id == id) {
                            selectedNote = notes.firstOrNull()
                        }
                    }
                )
                EditorPanel(
                    note = selectedNote,
                    onNoteChange = { saveDebounced(it) }
                )
            }
        }
    }
}
