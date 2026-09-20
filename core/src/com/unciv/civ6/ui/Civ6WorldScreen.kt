package com.unciv.civ6.ui

import com.unciv.civ6.engine.GameInfoV2
import com.unciv.civ6.engine.TurnManager

/**
 * Civ6 WorldScreen placeholder - bridges GameInfoV2 to LibGDX UI.
 * Replaces WorldScreen.kt Civ5 logic, keeps topbar/bottombar rendering.
 */
class Civ6WorldScreen(val gameInfo: GameInfoV2) {
    private val turnManager = TurnManager(gameInfo)

    fun nextTurn() {
        turnManager.nextTurn()
        // In real UI: update topbar (turns), city banners (housing/amenity/loyalty), tech/civic progress
    }

    fun cityInfo(): List<String> {
        return gameInfo.cities.map { city ->
            val pop = city.populationState()
            "${city.name} Pop:${city.population} Housing:${pop.housing}(${pop.housingGrowthModifier}) Amenities:${pop.amenities}/${pop.amenityLevel} Districts:${city.districts.size}/${city.maxDistrictSlots()}"
        }
    }

    fun techProgress(techName: String): String {
        val p = gameInfo.techManager?.progress(techName) ?: 0
        val done = gameInfo.techManager?.isCompleted(techName) ?: false
        return "$techName: $p ${if (done) "✓" else ""}"
    }

    fun governmentInfo(): String {
        val gov = gameInfo.governmentManager?.currentGovernment ?: "Chiefdom"
        val slots = gameInfo.governmentManager?.slottedPolicies()?.joinToString() ?: ""
        return "Gov: $gov Slots: [$slots]"
    }
}
