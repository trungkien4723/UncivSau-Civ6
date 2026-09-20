package com.unciv.civ6.ui

import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.unciv.civ6.engine.Civ6Files
import com.unciv.civ6.engine.GameInfoV2
import com.unciv.ui.screens.basescreen.BaseScreen
import com.unciv.ui.components.extensions.toLabel

/**
 * Save/Load for Civ6 v10 - uses Civ6Files, not UncivFiles v4.
 */
class Civ6SaveLoad(private val gameInfo: GameInfoV2) : BaseScreen() {
    private val files = Civ6Files()

    init {
        val table = com.badlogic.gdx.scenes.scene2d.ui.Table()
        table.add("Save/Load v10".toLabel()).row()
        table.add("Game ${gameInfo.gameId} Turn ${gameInfo.turns}".toLabel()).row()
        stage.addActor(table)
        table.setFillParent(true)
    }

    fun save(name: String): String {
        return try {
            val json = files.gameInfoToString(gameInfo)
            // In real impl, write to FileHandle
            "Saved $name ${json.length} chars"
        } catch (e: Exception) { "Save failed: ${e.message}" }
    }
}
