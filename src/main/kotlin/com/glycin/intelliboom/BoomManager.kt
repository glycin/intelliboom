package com.glycin.intelliboom

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.EDT
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.util.TextRange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.Point
import java.awt.image.BufferedImage
import javax.swing.JScrollPane
import javax.swing.SwingUtilities
import kotlin.math.abs

private const val FPS = 120L
private const val EXP_1_SIZE = 18
private const val EXP_2_SIZE = 23
private const val EXP_3_SIZE = 21
private const val EXP_STRENGTH = 30

class BoomManager(
    private val scope: CoroutineScope,
    private val boomSettings: BoomSettings,
    private val soundPlayer: SoundPlayer,
): Disposable {

    private val explosion1 = arrayOfNulls<BufferedImage>(EXP_1_SIZE)
    private val explosion2 = arrayOfNulls<BufferedImage>(EXP_2_SIZE)
    private val explosion3 = arrayOfNulls<BufferedImage>(EXP_3_SIZE)

    private val explosions = listOf(explosion1, explosion2, explosion3)

    init {
        loadExplosionSprites()
    }

    fun explode(mousePosition: Point, editor: Editor) {
        if(editor.document.textLength <= 0) return
        if(editor.document.text.all { it.isWhitespace() || it == '\n' }) return
        val project = editor.project ?: return

        val contentComponent = editor.contentComponent
        editor.settings.isVirtualSpace = true

        val yScroll = editor.scrollingModel.verticalScrollOffset
        val objs = getLinesInRange(editor, mousePosition)
        BoomWriter.clear(editor, project, objs)

        val boomComponent = BoomDrawComponent(
            explosionImages = explosions.random(),
            explosionObjects = objs,
            position = mousePosition.toVec2(editor.scrollingModel),
            scope = scope,
            fps = FPS,
            finishedCallback = {
                it.cleanup()
                contentComponent.remove(it)
                contentComponent.revalidate()
                contentComponent.repaint()
                contentComponent.requestFocusInWindow()

                scope.launch(Dispatchers.EDT) {
                    BoomWriter.writeText(objs, editor, project, yScroll)
                }
            }
        ).apply {
            bounds = (SwingUtilities.getAncestorOfClass(JScrollPane::class.java, editor.contentComponent) as JScrollPane).viewport.viewRect
            preferredSize = contentComponent.size
            isOpaque = false
        }

        if(boomSettings.state.soundOn) {
            soundPlayer.playExplosion()
        }

        contentComponent.add(boomComponent)
        contentComponent.revalidate()
        contentComponent.repaint()

        boomComponent.requestFocusInWindow()
        editor.settings.isVirtualSpace = false
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

    private fun getLinesInRange(editor: Editor, explosionCenter: Point): List<MovableObject> {
        val document = editor.document
        val explosionLine = editor.xyToLogicalPosition(explosionCenter).line

        return (0 until  document.lineCount).flatMap { line ->
            val startOffset = document.getLineStartOffset(line)
            val endOffset = document.getLineEndOffset(line)
            val distance = abs(editor.offsetToLogicalPosition(startOffset).line - explosionLine)

            document.getText(TextRange(startOffset, endOffset)).mapIndexedNotNull { index, c ->
                if(c.isWhitespace()) return@mapIndexedNotNull null
                if(distance > EXP_STRENGTH) return@mapIndexedNotNull null

                val charPos = editor.offsetToXY(startOffset + index)
                MovableObject(
                    position = charPos.toVec2(editor.scrollingModel),
                    width = getCharWidth(editor, c),
                    height = editor.lineHeight,
                    char = c.toString(),
                    lineNumber = line,
                )
            }
        }
    }

    private fun getCharWidth(editor: Editor, c: Char): Int {
        val fontMetrics = editor.contentComponent.getFontMetrics(editor.colorsScheme.getFont(EditorFontType.PLAIN))
        return fontMetrics.charWidth(c)
    }
}