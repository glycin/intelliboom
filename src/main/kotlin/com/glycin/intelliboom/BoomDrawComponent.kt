package com.glycin.intelliboom

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.Gray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JComponent
import javax.swing.JLabel
import kotlin.math.*

class BoomDrawComponent(
    private val explosionImages: Array<BufferedImage?>,
    private val explosionObjects: List<MovableObject>,
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
        createLabels()

        scope.launch {
            while (active) {
                showAnimation(4)
                repaint()
                delay(deltaTime)
            }
        }

        explode(position, scope)
    }

    fun cleanup() {
        currentSprite = null
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if(g is Graphics2D) {
            val pos = Vec2(position.x - (width / 2), position.y - (height / 2))
            g.drawImage(currentSprite, pos.x.roundToInt(), pos.y.roundToInt(), width, height, null)
            drawObjects(g)
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

    private fun createLabels(){
        val scheme = EditorColorsManager.getInstance().globalScheme
        val fontPreferences = scheme.fontPreferences

        explosionObjects.forEach { boom ->
            val objLabel = JLabel(boom.char)
            objLabel.font = Font(fontPreferences.fontFamily, 0, fontPreferences.getSize(fontPreferences.fontFamily))
            objLabel.setBounds(boom.position.x.roundToInt(), boom.position.y.roundToInt(), boom.width, boom.height)
            objLabel.isVisible = false
            add(objLabel)
            boom.label = objLabel
            boom.show()
        }
        repaint()
    }

    private fun explode(explosionPos: Vec2, scope: CoroutineScope) {
        val start = System.currentTimeMillis()
        val duration = 1000 //millis
        val endTime = start + duration

        scope.launch (Dispatchers.Main) {
            delay(50) // A little delay to make the effect match the gif
            while(System.currentTimeMillis() < endTime) {
                explosionObjects.onEach { b ->
                    val centerPos = b.midPoint()
                    val distance = Vec2.distance(centerPos, explosionPos)

                    if(distance < explosionRadius) {
                        val forceMagnitude = explosionForce * (explosionRadius - distance) / explosionDecay
                        b.moveWithForce(forceMagnitude, explosionPos)
                    }else {
                        b.force = 0.0f
                    }

                    handleCollisions(b)
                }
                delay(deltaTime)
            }
            explosionObjects.forEach { it.rest() }
        }.invokeOnCompletion {
            //explosionWriter.writeText(boomObjects)
        }
    }

    // Fake it till you make it physics...
    private fun handleCollisions(a: MovableObject) {
        val midPointA = a.midPoint()
        explosionObjects
            .filter {
                it.intersects(a)
            }
            .onEach { b ->
                val midPointB = b.midPoint()
                val angle = atan2((midPointB.y - midPointA.y).toDouble(), (midPointB.x - midPointA.x).toDouble())

                val overlapX = a.maxX() - b.minX()
                val overlapY = a.maxY() - b.minY()
                val moveDistance = min(overlapX, overlapY)  / 2

                val moveX = (moveDistance * cos(angle)).toInt()
                val moveY = (moveDistance * sin(angle)).toInt()

                a.position -= Vec2(moveX.toFloat(), moveY.toFloat())
                b.position += Vec2(moveX.toFloat(), moveY.toFloat())
                a.velocity *= -1
                b.velocity *= -1
            }
    }

    // Used for debugging
    private fun drawObjects(g: Graphics2D) {
        explosionObjects.forEach { boom ->
            g.color = Gray._117
            g.drawRect(boom.position.x.roundToInt(), boom.position.y.roundToInt(), boom.width, boom.height)
        }
    }
}