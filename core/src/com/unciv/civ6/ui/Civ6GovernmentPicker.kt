package com.unciv.civ6.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.unciv.civ6.domain.government.GovernmentManager
import com.unciv.ui.screens.basescreen.BaseScreen
import com.unciv.ui.components.extensions.toLabel

/**
 * Government picker - shows policy slots Military/Economic/Diplomatic/Wildcard.
 */
class Civ6GovernmentPicker(private val govManager: GovernmentManager) : BaseScreen() {
    init {
        val table = Table()
        table.add("Government: ${govManager.currentGovernment}".toLabel()).row()
        table.add("Slots: ${govManager.slottedPolicies().joinToString()}".toLabel()).row()
        stage.addActor(table)
        table.setFillParent(true)
    }
}
