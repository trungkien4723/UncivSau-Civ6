package com.unciv.civ6.console

import com.unciv.civ6.engine.GameInfoV2

/**
 * Console commands for Civ6 debug - replaces DevConsole for Civ5.
 */

class Civ6Console(private val gameInfo: GameInfoV2) {
    fun execute(command: String): String {
        val parts = command.trim().split(" ")
        return when (parts[0].lowercase()) {
            "help" -> "Commands: housing, amenity, tech, civic, loyalty, addcity, nextturn"
            "housing" -> {
                val city = gameInfo.cities.firstOrNull() ?: return "No city"
                "Housing ${city.housing()} Pop ${city.population} ${city.populationState()}"
            }
            "amenity" -> {
                val city = gameInfo.cities.firstOrNull() ?: return "No city"
                val pop = city.populationState()
                "Amenities ${city.amenities()} Level ${pop.amenityLevel}"
            }
            "tech" -> gameInfo.techManager?.let { "Techs: ${it}" } ?: "No tech"
            "nextturn" -> { gameInfo.nextTurn(); "Turn ${gameInfo.turns}" }
            "addcity" -> {
                val name = parts.getOrNull(1) ?: "City${gameInfo.cities.size+1}"
                val city = com.unciv.civ6.domain.city.City(name, 1, hasFreshWater = true)
                gameInfo.addCity(city)
                "Added $name"
            }
            else -> "Unknown: $command"
        }
    }
}
