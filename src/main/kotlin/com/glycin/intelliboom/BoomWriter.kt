package com.glycin.intelliboom

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import java.awt.Point
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object BoomWriter {

    fun writeText(objs: List<MovableObject>, editor: Editor, project: Project, yOffset: Int) {
        val logicalPositions = objs.associateBy {
            val x = editor.xyToLogicalPosition(it.position.toPoint(yOffset = yOffset)).column
            LogicalPosition(getLogicalLineFromY(editor, it.position.y.roundToInt() + yOffset), x)
        }

        // We add an extra line/column otherwise the max character of each will get discarded :(
        val minLine = logicalPositions.keys.minOf { it.line }
        val maxLine = logicalPositions.keys.maxOf { it.line } + 1

        val sb = StringBuilder()

        (0 until max(maxLine, editor.document.lineCount)).forEach { line ->
            if(line in minLine..maxLine) {
                for (column in 0 until logicalPositions.maxOf { it.key.column } + 1) {
                    val foundPos = logicalPositions[LogicalPosition(line, column)]
                    sb.append(foundPos?.char ?: " ")
                }
                sb.appendLine()

            } else {
                val startOffset = editor.document.getLineStartOffset(line)
                val endOffset = editor.document.getLineEndOffset(line)
                sb.append(editor.document.getText(TextRange(startOffset, endOffset)))
                sb.appendLine()
            }

        }

        val s = sb.toString()

        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.replaceString(0, editor.document.textLength, s)
            }
        }
    }

    fun clear(editor: Editor, project: Project, expObjects: List<MovableObject>) {
        val sb = StringBuilder()
        val minLine = expObjects.minOf { it.lineNumber }
        val maxLine = expObjects.maxOf { it.lineNumber }

        (0 until editor.document.lineCount).forEach { line ->
            val startOffset = editor.document.getLineStartOffset(line)
            val endOffset = editor.document.getLineEndOffset(line)

            editor.document.getText(TextRange(startOffset, endOffset)).forEach {
                sb.append(if(line in minLine..maxLine) ' ' else it)
            }

            sb.appendLine()
        }

        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.replaceString(0, editor.document.textLength, sb.toString())
            }
        }
    }

    // Calculate the line ourselves because the line from `editor.xyToLogicalPosition` is kinda wonky
    private fun getLogicalLineFromY(editor: Editor, y: Int): Int {
        val lineHeight = editor.lineHeight
        val visibleArea = editor.scrollingModel.visibleArea

        val firstVisibleLine = editor.xyToLogicalPosition(Point(0, visibleArea.y)).line
        val estimatedLine = firstVisibleLine + ((y - visibleArea.y) / lineHeight)

        return max(0, estimatedLine)
    }
}