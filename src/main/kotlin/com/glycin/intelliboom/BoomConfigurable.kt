package com.glycin.intelliboom

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.util.ui.JBUI
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

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
            gbc.anchor = GridBagConstraints.NORTHWEST
            gbc.fill = GridBagConstraints.HORIZONTAL
            gbc.weightx = 1.0
            gbc.insets = JBUI.insetsBottom(5)

            // Add info label
            gbc.gridy = 0
            val infoLabel = JLabel("Ctrl + Right Click anywhere in a file to trigger an explosion!")
            add(infoLabel, gbc)

            // Add separator
            gbc.gridy = 1
            val separator = JSeparator()
            add(separator, gbc)

            gbc.gridy = 2
            gbc.fill = GridBagConstraints.NONE

            // Add sound checkbox
            soundCheckBox = JCheckBox("Enable explosion sound").apply {
                isSelected = settings.soundOn
                addActionListener {
                    settings.soundOn = isSelected
                }
            }
            add(soundCheckBox, gbc)

            gbc.gridy = 3
            gbc.weighty = 1.0
            add(Box.createVerticalGlue(), gbc)
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