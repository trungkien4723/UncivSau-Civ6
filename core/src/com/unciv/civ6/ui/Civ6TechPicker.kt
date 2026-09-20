package com.unciv.civ6.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.unciv.civ6.domain.tech.TechManager
import com.unciv.civ6.domain.tech.CivicManager
import com.unciv.ui.screens.basescreen.BaseScreen
import com.unciv.ui.components.extensions.toLabel
import com.unciv.ui.components.extensions.toTextButton

/**
 * Tech/Civic picker for Civ6 - shows 2 trees, eureka/inspiration progress.
 */
class Civ6TechPicker(
    private val techManager: TechManager,
    private val civicManager: CivicManager
) : BaseScreen() {
    init {
        val table = Table()
        table.add("Technology Tree".toLabel()).row()
        // In real UI, show columns by era, cost, prerequisites, boost
        table.add("Civic Tree".toLabel()).row()
        table.add("Pick Tech/Civic to research".toLabel()).row()
        stage.addActor(table)
        table.setFillParent(true)
    }
}
