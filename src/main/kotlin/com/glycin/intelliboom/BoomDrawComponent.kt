package com.glycin.intelliboom

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JComponent
import kotlin.math.roundToInt

class BoomDrawComponent(
    private val explosionImages: Array<BufferedImage?>,
    private val position: Vec2,
    private val width: Int = 250,
    private val height: Int = 250,
    private val finishedCallback: (BoomDrawComponent) -> Unit,
    scope: CoroutineScope,
    fps: Long,
): JComponent() {
    private val deltaTime = 1000L / fps

    private val explosionDecay = 50
    private val explosionForce = 5
    private val explosionRadius = 200

    private var currentAnimationIndex = 0
    private var skipFrameCount = 0
    private var currentSprite : BufferedImage? = explosionImages[0]

    private var active = true

    init {
        scope.launch {
            while (active) {
                println("BOOM")
                showAnimation(4)
                repaint()
                delay(deltaTime)
            }
        }
    }

    fun cleanup() {
        currentSprite = null
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            val pos = Vec2(position.x - (width / 2), position.y - (height / 2))
            g.drawImage(currentSprite, pos.x.roundToInt(), pos.y.roundToInt(), width, height, null)
        }
    }

    private fun showAnimation(frameDelay: Int) {
        skipFrameCount++
        if(skipFrameCount % frameDelay == 0) {
            currentAnimationIndex++
        }

        if(currentAnimationIndex >= explosionImages.size - 1) {
            currentAnimationIndex = 0
            skipFrameCount = 0
            active = false
            finishedCallback.invoke(this)
        }
        currentSprite = explosionImages[currentAnimationIndex]!!
    }
}