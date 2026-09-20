package com.unciv.civ6.engine

import com.unciv.civ6.domain.city.City
import com.unciv.civ6.domain.loyalty.LoyaltyPressure

/**
 * TurnManager v2 - tích hợp city housing/amenity, loyalty, era, government tick.
 * Thay thế core/src/com/unciv/logic/civilization/managers/TurnManager.kt Civ5.
 */
open class TurnManager(protected val gameInfo: GameInfoV2) {

    open fun nextTurn() {
        gameInfo.nextTurn()
        // Tick cities
        for (city in gameInfo.cities) {
            updateCity(city)
        }
        // Loyalty
        updateLoyalties()
        // Governor tick
        gameInfo.governmentManager?.let {
            // GovernorManager tick via reflection? We'll call if needed
        }
        // Tech/Civic progress tick is handled via science/culture yields per turn
    }

    private fun updateCity(city: City) {
        val popState = city.populationState()
        // Housing growth modifier affects food -> population, caller will apply
        // Amenity level affects yields, handled in CityStats update elsewhere
        // Placeholder: log state
    }

    private fun updateLoyalties() {
        // Simple foreign pressure mock: for each city, sum pressure from nearest foreign cities
        // In real game, need map distance. Here simplified to 0 foreign if no data.
        for ((name, loyalty) in gameInfo.cityLoyalties) {
            val city = gameInfo.cities.find { it.name == name } ?: continue
            val ownPressure = LoyaltyPressure(city.population, 0).pressure()
            val foreignPressure = 0f // TODO: compute from map
            loyalty.update(ownPressure, foreignPressure, hasGovernor = false)
        }
    }

    fun addScience(science: Int, techName: String) {
        gameInfo.techManager?.addProgress(techName, science)
    }

    fun addCulture(culture: Int, civicName: String) {
        gameInfo.civicManager?.addProgress(civicName, culture)
    }

    fun triggerEureka(techName: String) { gameInfo.techManager?.triggerEureka(techName) }
    fun triggerInspiration(civicName: String) { gameInfo.civicManager?.triggerInspiration(civicName) }
}
