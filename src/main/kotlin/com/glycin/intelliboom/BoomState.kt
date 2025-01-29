package com.glycin.intelliboom

import com.intellij.openapi.components.BaseState
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(name = "IntelliboomSettings", storages = [Storage("intelliboomSettings.xml")])
class BoomSettings: SimplePersistentStateComponent<BoomState>(BoomState())
class BoomState: BaseState() {
    var soundOn : Boolean by property(true)
}