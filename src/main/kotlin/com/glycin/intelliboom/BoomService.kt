package com.glycin.intelliboom

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.EditorFactory
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.APP)
class BoomService(
    private val scope: CoroutineScope,
): Disposable {

    private var bm: BoomManager? = null
    private lateinit var boomClickListener : BoomClickListener

    fun init() {
        if(bm != null) { return } // The manager is already registered, no need to do that again
        bm = BoomManager(scope)
        boomClickListener = BoomClickListener(bm)
        EditorFactory.getInstance().eventMulticaster.addEditorMouseListener(boomClickListener, this)
    }

    override fun dispose() {
        bm?.dispose()
        EditorFactory.getInstance().eventMulticaster.removeEditorMouseListener(boomClickListener)
        bm = null
    }
}