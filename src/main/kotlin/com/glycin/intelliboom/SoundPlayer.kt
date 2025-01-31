package com.glycin.intelliboom

import com.intellij.openapi.diagnostic.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import kotlin.random.Random

class SoundPlayer(
    scope: CoroutineScope,
) {
    private val log = Logger.getInstance(this::class.java)

    private val explSound1 : Clip? = AudioSystem.getClip()
    private val explSound2 : Clip? = AudioSystem.getClip()
    private val explSound3 : Clip? = AudioSystem.getClip()

    init {
        scope.launch(Dispatchers.IO) {
            try{
                loadClip("boom1.wav") {
                    explSound1?.open(AudioSystem.getAudioInputStream(it))
                }

                loadClip("boom2.wav") {
                    explSound2?.open(AudioSystem.getAudioInputStream(it))
                }

                loadClip("boom3.wav") {
                    explSound3?.open(AudioSystem.getAudioInputStream(it))
                }
            } catch (e: Exception) {
                log.error("Failed to load clip!", e)
            }
        }
    }

    fun playExplosion() {
        try {
            when (Random.nextInt(1, 3)) {
                1 -> explSound1
                2 -> explSound2
                3 -> explSound3
                else -> explSound1
            }?.let { sound ->
                if(sound.isRunning) { sound.stop() }
                sound.framePosition = 0
                sound.start()
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadClip(soundName: String, assign: (stream: BufferedInputStream) -> Unit) {
        this::class.java.getResourceAsStream("/Sounds/$soundName")?.use { stream ->
            BufferedInputStream(stream).use { bis ->
                assign(bis)
            }
        }
    }
}