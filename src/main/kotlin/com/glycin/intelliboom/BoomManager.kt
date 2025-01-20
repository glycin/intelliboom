package com.glycin.intelliboom

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.EditorFactory
import kotlinx.coroutines.CoroutineScope

private const val FPS = 120L

class BoomManager(
    private val scope: CoroutineScope,
): Disposable {

    private var boomClickListener : BoomClickListener = BoomClickListener()

    init {
        println("Adding mouse listener")
        EditorFactory.getInstance().eventMulticaster.addEditorMouseListener(boomClickListener, this)
    }

    override fun dispose() {
        EditorFactory.getInstance().eventMulticaster.removeEditorMouseListener(boomClickListener)
    }
}