package com.unciv.civ6.ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.unciv.civ6.engine.GameInfoV2
import com.unciv.ui.screens.basescreen.BaseScreen
import com.unciv.ui.components.extensions.toLabel

/**
 * Minimal WorldScreen for Civ6 - shows GameInfoV2 and allows nextTurn.
 * Replaces full WorldScreen.kt for initial playable test.
 */
class Civ6WorldScreenMinimal(val gameInfo: GameInfoV2) : BaseScreen() {
    init {
        val table = Table()
        table.add("UncivSau-Civ6 - Minimal WorldScreen".toLabel()).row()
        table.add("Turn: ${gameInfo.turns} Cities: ${gameInfo.cities.size}".toLabel()).row()
        for (city in gameInfo.cities) {
            table.add("${city.name} Pop:${city.population} Housing:${city.housing()}".toLabel()).row()
        }
        table.add("Press Next Turn to test TurnManager".toLabel()).row()
        stage.addActor(table)
        table.setFillParent(true)
    }

    fun nextTurn() {
        gameInfo.nextTurn()
    }
}
