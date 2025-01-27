package com.glycin.intelliboom

import com.intellij.openapi.editor.event.EditorMouseEvent
import com.intellij.openapi.editor.event.EditorMouseEventArea
import com.intellij.openapi.editor.event.EditorMouseListener
import java.awt.event.InputEvent
import java.awt.event.MouseEvent


class BoomClickListener(
    private val bm: BoomManager?
): EditorMouseListener {

    override fun mouseClicked(e: EditorMouseEvent) {
        if(e.area != EditorMouseEventArea.EDITING_AREA || bm == null) return

        val mouseEvent = e.mouseEvent
        if(mouseEvent.button == MouseEvent.BUTTON3 &&
            mouseEvent.modifiersEx == InputEvent.CTRL_DOWN_MASK) {
            bm.explode(mouseEvent.point, e.editor)
        }
    }
}