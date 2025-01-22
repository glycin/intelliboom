package com.glycin.intelliboom

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Editor
import kotlinx.coroutines.CoroutineScope
import java.awt.Point
import java.awt.image.BufferedImage

private const val FPS = 120L
private const val EXP_1_SIZE = 18
private const val EXP_2_SIZE = 23
private const val EXP_3_SIZE = 21

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
            position = mousePosition.toVec2(), //TODO: Add the scrollofsset
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
            bounds = contentComponent.bounds
            isOpaque = false
        }
        contentComponent.add(boomComponent)
        contentComponent.revalidate()
        contentComponent.repaint()

        boomComponent.requestFocusInWindow()
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
}