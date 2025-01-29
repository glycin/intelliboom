package com.glycin.intelliboom

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.SearchableConfigurable
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel

class BoomConfigurable: SearchableConfigurable {
    private var panel: JPanel? = null
    private var soundCheckBox: JCheckBox? = null

    private val settings = ApplicationManager.getApplication().getService(BoomSettings::class.java).state

    override fun getId(): String {
        return "Intelliboom"
    }

    override fun getDisplayName(): String {
        return "Code Exploder Settings"
    }

    override fun createComponent(): JComponent {

        panel = JPanel().apply {
            layout = GridBagLayout()
            val gbc = GridBagConstraints()

            gbc.gridx = 0
            gbc.gridy = 0
            gbc.weightx = 1.0
            gbc.anchor = GridBagConstraints.NORTHWEST

            soundCheckBox = JCheckBox("Enable explosion sound").apply {
                isSelected = settings.soundOn
                addActionListener {
                    settings.soundOn = isSelected
                }
            }
            add(soundCheckBox, gbc)
        }
        return panel!!
    }

    override fun isModified(): Boolean {
        return soundCheckBox?.isSelected != settings.soundOn
    }

    override fun apply() {
        settings.soundOn = soundCheckBox?.isSelected == true
    }

    override fun reset() {
        soundCheckBox?.isSelected = settings.soundOn
    }

    override fun disposeUIResources() {
        panel = null
        soundCheckBox = null
    }
}