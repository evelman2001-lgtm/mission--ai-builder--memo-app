package com.macnotes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.macnotes.model.Note
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun NotesList(
    notes: List<Note>,
    selectedId: String?,
    onSelect: (Note) -> Unit,
    onCreate: () -> Unit,
    onDelete: (String) -> Unit
) {
    val fmt = remember { SimpleDateFormat("MM/dd HH:mm") }

    Column(
        Modifier
            .fillMaxHeight()
            .width(220.dp)
            .background(Color(0xFFF0F0F5))
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("메모", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A2E))
            IconButton(onClick = onCreate, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Add, contentDescription = "새 메모", tint = Color(0xFF7C83FD))
            }
        }

        Divider(color = Color(0xFFDDDDEE))

        LazyColumn(Modifier.fillMaxSize()) {
            items(notes, key = { it.id }) { note ->
                NoteItem(
                    note = note,
                    isSelected = note.id == selectedId,
                    formattedDate = fmt.format(Date(note.updatedAt)),
                    onClick = { onSelect(note) },
                    onDelete = { onDelete(note.id) }
                )
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    isSelected: Boolean,
    formattedDate: String,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("메모 삭제") },
            text = { Text("\"${note.title}\" 메모를 삭제할까요?") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) { Text("삭제", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
            }
        )
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFFE8E9FF) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = note.title.ifBlank { "제목 없음" },
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF1A1A2E)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = note.content.lines().firstOrNull { it.isNotBlank() }?.take(40) ?: "",
                fontSize = 11.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(formattedDate, fontSize = 10.sp, color = Color(0xFFAAAAAA))
        }
        IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "삭제", tint = Color(0xFFCCCCCC), modifier = Modifier.size(16.dp))
        }
    }
}
