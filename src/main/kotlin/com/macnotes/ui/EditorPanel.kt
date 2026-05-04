package com.macnotes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.macnotes.model.Note

val FONT_FAMILIES = listOf("System", "Serif", "Monospace")
val FONT_SIZES = listOf(11, 12, 13, 14, 15, 16, 18, 20, 24)
val TEXT_COLORS = mapOf(
    "기본" to "#1A1A2E",
    "짙은 회색" to "#4A4A4A",
    "파랑" to "#1565C0",
    "초록" to "#2E7D32",
    "빨강" to "#C62828",
    "보라" to "#6A1B9A"
)

fun fontFamilyFromName(name: String): FontFamily = when (name) {
    "Serif" -> FontFamily.Serif
    "Monospace" -> FontFamily.Monospace
    else -> FontFamily.Default
}

fun parseColor(hex: String): Color {
    val clean = hex.trimStart('#')
    return try {
        Color(
            red = clean.substring(0, 2).toInt(16) / 255f,
            green = clean.substring(2, 4).toInt(16) / 255f,
            blue = clean.substring(4, 6).toInt(16) / 255f
        )
    } catch (e: Exception) {
        Color.Black
    }
}

@Composable
fun EditorPanel(
    note: Note?,
    onNoteChange: (Note) -> Unit
) {
    if (note == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("메모를 선택하거나 새로 만드세요", color = Color.Gray, fontSize = 16.sp)
        }
        return
    }

    var isPreview by remember(note.id) { mutableStateOf(false) }
    var fontFamily by remember(note.id) { mutableStateOf(note.fontFamily) }
    var fontSize by remember(note.id) { mutableStateOf(note.fontSize) }
    var textColor by remember(note.id) { mutableStateOf(note.textColor) }
    var content by remember(note.id) { mutableStateOf(note.content) }
    var title by remember(note.id) { mutableStateOf(note.title) }

    fun emitChange(
        newContent: String = content,
        newTitle: String = title,
        newFont: String = fontFamily,
        newSize: Int = fontSize,
        newColor: String = textColor
    ) {
        onNoteChange(
            note.copy(
                content = newContent,
                title = newTitle,
                fontFamily = newFont,
                fontSize = newSize,
                textColor = newColor
            )
        )
    }

    Column(Modifier.fillMaxSize().background(Color(0xFFFAFAFA))) {
        // Title bar
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = title,
                onValueChange = { title = it; emitChange(newTitle = it) },
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                ),
                modifier = Modifier.weight(1f),
                singleLine = true,
                decorationBox = { inner ->
                    if (title.isEmpty()) Text("제목 없음", color = Color.LightGray, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    inner()
                }
            )
        }

        Divider(color = Color(0xFFE0E0E0))

        // Toolbar
        EditorToolbar(
            fontFamily = fontFamily,
            fontSize = fontSize,
            textColor = textColor,
            isPreview = isPreview,
            onFontFamilyChange = { fontFamily = it; emitChange(newFont = it) },
            onFontSizeChange = { fontSize = it; emitChange(newSize = it) },
            onTextColorChange = { textColor = it; emitChange(newColor = it) },
            onPreviewToggle = { isPreview = !isPreview }
        )

        Divider(color = Color(0xFFE0E0E0))

        // Content area
        if (isPreview) {
            MarkdownPreview(
                content = content,
                fontSize = fontSize,
                fontFamily = fontFamily,
                textColor = textColor,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            BasicTextField(
                value = content,
                onValueChange = { content = it; emitChange(newContent = it) },
                textStyle = TextStyle(
                    fontSize = fontSize.sp,
                    fontFamily = fontFamilyFromName(fontFamily),
                    color = parseColor(textColor),
                    lineHeight = (fontSize * 1.7).sp
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                decorationBox = { inner ->
                    if (content.isEmpty()) {
                        Text(
                            "메모를 입력하세요... (마크다운 지원)",
                            color = Color(0xFFBBBBBB),
                            fontSize = fontSize.sp
                        )
                    }
                    inner()
                }
            )
        }
    }
}

@Composable
fun EditorToolbar(
    fontFamily: String,
    fontSize: Int,
    textColor: String,
    isPreview: Boolean,
    onFontFamilyChange: (String) -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onTextColorChange: (String) -> Unit,
    onPreviewToggle: () -> Unit
) {
    var showFontMenu by remember { mutableStateOf(false) }
    var showSizeMenu by remember { mutableStateOf(false) }
    var showColorMenu by remember { mutableStateOf(false) }

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Font family
        Box {
            OutlinedButton(
                onClick = { showFontMenu = true },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(fontFamily, fontSize = 13.sp)
            }
            DropdownMenu(expanded = showFontMenu, onDismissRequest = { showFontMenu = false }) {
                FONT_FAMILIES.forEach { f ->
                    DropdownMenuItem(
                        text = { Text(f) },
                        onClick = { onFontFamilyChange(f); showFontMenu = false }
                    )
                }
            }
        }

        // Font size
        Box {
            OutlinedButton(
                onClick = { showSizeMenu = true },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("${fontSize}px", fontSize = 13.sp)
            }
            DropdownMenu(expanded = showSizeMenu, onDismissRequest = { showSizeMenu = false }) {
                FONT_SIZES.forEach { s ->
                    DropdownMenuItem(
                        text = { Text("${s}px") },
                        onClick = { onFontSizeChange(s); showSizeMenu = false }
                    )
                }
            }
        }

        // Text color
        Box {
            OutlinedButton(
                onClick = { showColorMenu = true },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Box(
                    Modifier
                        .size(14.dp)
                        .background(parseColor(textColor), shape = androidx.compose.foundation.shape.CircleShape)
                )
                Spacer(Modifier.width(6.dp))
                Text("색상", fontSize = 13.sp)
            }
            DropdownMenu(expanded = showColorMenu, onDismissRequest = { showColorMenu = false }) {
                TEXT_COLORS.forEach { (label, hex) ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(Modifier.size(14.dp).background(parseColor(hex), shape = androidx.compose.foundation.shape.CircleShape))
                                Text(label)
                            }
                        },
                        onClick = { onTextColorChange(hex); showColorMenu = false }
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Preview toggle
        FilterChip(
            selected = isPreview,
            onClick = onPreviewToggle,
            label = { Text(if (isPreview) "편집" else "미리보기", fontSize = 13.sp) }
        )
    }
}
