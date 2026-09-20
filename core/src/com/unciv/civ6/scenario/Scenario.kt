package com.unciv.civ6.scenario

import com.unciv.civ6.engine.GameInfoV2
import com.unciv.civ6.Civ6GameStarter

/**
 * Scenario for Civ6v2 - replaces Scenarios for Civ5.
 */

data class Scenario(
    val name: String,
    val description: String,
    val mapFile: String,
    val startingEra: String = "Ancient",
    val victoryType: String = "Science"
)

object ScenarioLoader {
    fun load(name: String): GameInfoV2 {
        // In real impl: load mapFile + scenario JSON, create GameInfoV2 with preset cities/techs
        val info = Civ6GameStarter.newGame()
        // Scenario-specific setup could be added here
        return info
    }

    fun list(): List<Scenario> = listOf(
        Scenario("Earth 2024", "Real Earth map", "earth2024.json"),
        Scenario("Mediterranean", "Mediterranean scenario", "med.json")
    )
}
