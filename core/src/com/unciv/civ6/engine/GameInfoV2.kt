package com.unciv.civ6.engine

import com.unciv.civ6.domain.city.City
import com.unciv.civ6.domain.government.GovernmentManager
import com.unciv.civ6.domain.loyalty.CityLoyalty
import com.unciv.civ6.domain.tech.CivicManager
import com.unciv.civ6.domain.tech.TechManager

/**
 * GameInfo v10 - root cho Civ6 clean. Không kế thừa legacy GameInfo.kt Civ5.
 */
data class GameInfoV2(
    var version: Civ6CompatibilityVersion = Civ6CompatibilityVersion.CURRENT,
    var gameId: String = java.util.UUID.randomUUID().toString(),
    var turns: Int = 0,
    var currentPlayer: String = "Player1",
    val cities: MutableList<City> = mutableListOf(),
    val cityLoyalties: MutableMap<String, CityLoyalty> = mutableMapOf(),
    var techManager: TechManager? = null,
    var civicManager: CivicManager? = null,
    var governmentManager: GovernmentManager? = null,
    var eraScore: Int = 0
) {
    fun nextTurn() { turns++ }

    fun addCity(city: City) {
        cities.add(city)
        cityLoyalties[city.name] = CityLoyalty(city.name, 100f, city.population, isCapital = cities.size == 1)
    }

    fun isCompatible(): Boolean = version.number <= Civ6CompatibilityVersion.CURRENT_NUMBER
}
