package com.macnotes.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import javax.swing.JEditorPane
import javax.swing.JScrollPane

private val parser = Parser.builder()
    .extensions(listOf(TablesExtension.create(), StrikethroughExtension.create(), TaskListItemsExtension.create()))
    .build()
private val renderer = HtmlRenderer.builder()
    .extensions(listOf(TablesExtension.create(), StrikethroughExtension.create(), TaskListItemsExtension.create()))
    .build()

fun markdownToHtml(markdown: String, fontSize: Int, fontFamily: String, textColor: String): String {
    val body = renderer.render(parser.parse(markdown))
    return """
        <html>
        <head>
        <style>
            body {
                font-family: '$fontFamily', -apple-system, BlinkMacSystemFont, sans-serif;
                font-size: ${fontSize}px;
                color: $textColor;
                margin: 20px 24px;
                line-height: 1.7;
                background: #FAFAFA;
            }
            h1 { font-size: ${fontSize + 10}px; border-bottom: 2px solid #E0E0E0; padding-bottom: 8px; }
            h2 { font-size: ${fontSize + 6}px; border-bottom: 1px solid #E0E0E0; padding-bottom: 6px; }
            h3 { font-size: ${fontSize + 3}px; }
            code {
                background: #F0F0F0;
                padding: 2px 6px;
                border-radius: 4px;
                font-family: 'JetBrains Mono', monospace;
                font-size: ${fontSize - 1}px;
            }
            pre {
                background: #282C34;
                color: #ABB2BF;
                padding: 16px;
                border-radius: 8px;
                overflow-x: auto;
            }
            pre code { background: none; color: inherit; padding: 0; }
            blockquote {
                border-left: 4px solid #7C83FD;
                margin: 0;
                padding: 4px 16px;
                color: #666;
                background: #F5F5FF;
                border-radius: 0 4px 4px 0;
            }
            table { border-collapse: collapse; width: 100%; }
            th, td { border: 1px solid #E0E0E0; padding: 8px 12px; text-align: left; }
            th { background: #F5F5F5; font-weight: 600; }
            a { color: #7C83FD; text-decoration: none; }
            a:hover { text-decoration: underline; }
            hr { border: none; border-top: 2px solid #E0E0E0; margin: 16px 0; }
        </style>
        </head>
        <body>$body</body>
        </html>
    """.trimIndent()
}

@Composable
fun MarkdownPreview(
    content: String,
    fontSize: Int,
    fontFamily: String,
    textColor: String,
    modifier: Modifier = Modifier
) {
    SwingPanel(
        modifier = modifier,
        factory = {
            val editor = JEditorPane("text/html", "").apply {
                isEditable = false
                putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true)
            }
            JScrollPane(editor).also {
                it.putClientProperty("editor", editor)
            }
        },
        update = { scrollPane ->
            val editor = scrollPane.getClientProperty("editor") as JEditorPane
            editor.text = markdownToHtml(content, fontSize, fontFamily, textColor)
            editor.caretPosition = 0
        }
    )
}
