package com.glycin.intelliboom

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import kotlinx.coroutines.CoroutineScope

@Service(Service.Level.APP)
class BoomService(
    private val scope: CoroutineScope,
): Disposable {

    private var bm: BoomManager? = null

    fun init() {
        bm = BoomManager(scope)
    }

    override fun dispose() {
        bm?.dispose()
        bm = null
    }
}