package com.unciv.app.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.unciv.UncivGame
import com.unciv.civ6.Civ6GameStarter
import com.unciv.civ6.ui.Civ6WorldScreenMinimal

class Civ6DesktopGame(config: Lwjgl3ApplicationConfiguration, customDataDir: String?) : UncivGame() {
    override fun create() {
        super.create()
        // Start Civ6 minimal game
        val gameInfo = Civ6GameStarter.newGame()
        val screen = Civ6WorldScreenMinimal(gameInfo)
        setScreen(screen)
    }
}
