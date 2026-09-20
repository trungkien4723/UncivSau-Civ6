package com.unciv.civ6.engine

import com.unciv.civ6.ai.CityAutomation
import com.unciv.civ6.ai.GovernmentAutomation
import com.unciv.civ6.ai.TechAutomation
import com.unciv.civ6.data.DistrictData

/**
 * TurnManager with AI integration for Civ6 clean.
 * Extends base TurnManager with automation hooks.
 */
class TurnManagerWithAI(
    gameInfo: GameInfoV2,
    private val allDistricts: List<DistrictData> = emptyList()
) : TurnManager(gameInfo) {
    private val aiFocus = CityAutomation.VictoryFocus.Science

    fun runAITurn(completedTechs: Set<String>, completedCivics: Set<String>) {
        // AI picks next tech/civic
        val techManager = gameInfo.techManager ?: return
        val civicManager = gameInfo.civicManager ?: return
        // Simplified: choose cheapest available (need techs map injected in real game)
        // AI city district choice
        for (city in gameInfo.cities) {
            val available = allDistricts.filter { city.canBuildDistrict(it) }
            val chosen = CityAutomation.chooseDistrict(city, available, aiFocus)
            // In real integration, would enqueue via CityConstructions
        }
        // Government auto-switch
        gameInfo.governmentManager?.let { mgr ->
            val newGov = GovernmentAutomation.shouldSwitchGovernment(mgr, completedCivics)
            if (newGov != null) mgr.switchGovernment(newGov, completedCivics)
        }
        nextTurn()
    }
}
