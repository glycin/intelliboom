package com.glycin.intelliboom

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.util.TextRange
import kotlinx.coroutines.CoroutineScope
import java.awt.Point
import java.awt.image.BufferedImage
import javax.swing.JScrollPane
import javax.swing.SwingUtilities
import kotlin.math.abs

private const val FPS = 120L
private const val EXP_1_SIZE = 18
private const val EXP_2_SIZE = 23
private const val EXP_3_SIZE = 21
private const val EXP_STRENGTH = 10

class BoomManager(
    private val scope: CoroutineScope,
): Disposable {

    private val explosion1 = arrayOfNulls<BufferedImage>(EXP_1_SIZE)
    private val explosion2 = arrayOfNulls<BufferedImage>(EXP_2_SIZE)
    private val explosion3 = arrayOfNulls<BufferedImage>(EXP_3_SIZE)

    private val explosions = listOf(explosion1, explosion2, explosion3)

    init {
        println("Init manager")
        loadExplosionSprites()
    }

    fun explode(mousePosition: Point, editor: Editor) {
        val contentComponent = editor.contentComponent
        editor.settings.isVirtualSpace = true

        val boomComponent = BoomDrawComponent(
            explosionImages = explosions.random(),
            position = mousePosition.toVec2(editor.scrollingModel),
            scope = scope,
            fps = FPS,
            finishedCallback = {
                it.cleanup()
                contentComponent.remove(it)
                contentComponent.revalidate()
                contentComponent.repaint()
                contentComponent.requestFocusInWindow()
            }
        ).apply {
            bounds = (SwingUtilities.getAncestorOfClass(JScrollPane::class.java, editor.contentComponent) as JScrollPane).viewport.viewRect
            isOpaque = false
        }
        contentComponent.add(boomComponent)
        contentComponent.revalidate()
        contentComponent.repaint()

        boomComponent.requestFocusInWindow()

        val lines = getLinesInRange(editor, mousePosition)
        val objs = getAffectedChars(editor, lines)
        println(objs)
    }

    override fun dispose() {
        explosion1.drop(EXP_1_SIZE)
        explosion2.drop(EXP_2_SIZE)
        explosion3.drop(EXP_3_SIZE)
    }

    private fun loadExplosionSprites() {
        SpriteSheetLoader("/Sprites/boom_1.png", EXP_1_SIZE)
            .loadSprites()
            .forEachIndexed { i, img -> explosion1[i] = img }

        SpriteSheetLoader("/Sprites/boom_2.png", EXP_2_SIZE)
            .loadSprites()
            .forEachIndexed { i, img -> explosion2[i] = img }

        SpriteSheetLoader("/Sprites/boom_3.png", EXP_3_SIZE)
            .loadSprites()
            .forEachIndexed { i, img -> explosion3[i] = img }
    }

    private fun getLinesInRange(editor: Editor, explosionCenter: Point): List<Int> {
        val document = editor.document
        val affectedLines = mutableListOf<Int>()
        for(line in 0 until document.lineCount) {
            val lineStartOffset = document.getLineStartOffset(line)
            val lineY = editor.offsetToXY(lineStartOffset).y
            val distance = abs(lineY - explosionCenter.y)

            if(distance <= EXP_STRENGTH * editor.lineHeight) {
                affectedLines.add(line)
            }
        }

        return affectedLines
    }

    //TODO: Do i also need to take a look at the charwdith?
    private fun getAffectedChars(editor: Editor, affectedLines: List<Int>): List<MovableObject> {
        val document = editor.document

        return affectedLines.flatMap { line ->
            val startOffset = document.getLineStartOffset(line)
            val endOffset = document.getLineEndOffset(line)
            document.getText(TextRange(startOffset, endOffset)).mapIndexedNotNull { index, c ->
                if(c.isWhitespace()) return@mapIndexedNotNull null
                val charPos = editor.offsetToXY(startOffset + index)
                MovableObject(
                    position = charPos.toVec2(),
                    width = 10,
                    height = editor.lineHeight,
                    char = c.toString(),
                )
            }
        }
    }
}