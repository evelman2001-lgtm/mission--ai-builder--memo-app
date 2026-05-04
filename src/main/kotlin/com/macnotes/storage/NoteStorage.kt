package com.macnotes.storage

import com.macnotes.model.Note
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

object NoteStorage {
    private val storageDir: File = File(
        System.getProperty("user.home"),
        "Library/Application Support/MacNotes"
    ).also { it.mkdirs() }

    private val metaFile = File(storageDir, "notes_meta.json")

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun loadAll(): List<Note> {
        if (!metaFile.exists()) return emptyList()
        return try {
            json.decodeFromString<List<Note>>(metaFile.readText())
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(notes: List<Note>) {
        metaFile.writeText(json.encodeToString(notes))
    }

    fun createNote(): Note {
        val note = Note(id = UUID.randomUUID().toString(), title = "새 메모")
        val all = loadAll().toMutableList()
        all.add(0, note)
        save(all)
        return note
    }

    fun updateNote(updated: Note): List<Note> {
        val all = loadAll().toMutableList()
        val idx = all.indexOfFirst { it.id == updated.id }
        if (idx >= 0) all[idx] = updated.copy(updatedAt = System.currentTimeMillis())
        else all.add(0, updated)
        save(all)
        return all
    }

    fun deleteNote(id: String): List<Note> {
        val all = loadAll().toMutableList()
        all.removeAll { it.id == id }
        save(all)
        return all
    }
}
