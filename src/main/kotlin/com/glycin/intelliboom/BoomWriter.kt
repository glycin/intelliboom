package com.glycin.intelliboom

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.project.Project
import java.awt.Point
import kotlin.math.max
import kotlin.math.roundToInt

object BoomWriter {

    //TODO: fix when scrolled
    fun writeText(objs: List<MovableObject>, editor: Editor, project: Project) {
        val logicalPositions = objs.associateBy {
            val x = editor.xyToLogicalPosition(it.position.toPoint(editor.scrollingModel)).column
            LogicalPosition(getLogicalLineFromY(editor, it.position.y.roundToInt()), x)
        }

        // We add an extra line/column otherwise the max character of each will get discarded :(
        val maxLine = logicalPositions.keys.maxOf { it.line } + 1
        val maxColumn = logicalPositions.keys.maxOf { it.column } + 1
        val sb = StringBuilder()

        for (line in 0 until maxLine) {
            for (column in 0 until maxColumn) {
                val foundPos = logicalPositions[LogicalPosition(line, column)]

                sb.append(foundPos?.char ?: " ")
            }
            sb.append("\n")
        }

        val s = sb.toString()

        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.insertString(0, s) //TODO: Maybe add enters to keep the original scroll view
            }
        }
    }

    fun clear(editor: Editor, project: Project) {
        ApplicationManager.getApplication().invokeLater {
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.deleteString(0, editor.document.textLength)
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