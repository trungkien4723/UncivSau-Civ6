package com.unciv.civ6

import com.unciv.civ6.data.BuildingData
import com.unciv.civ6.data.DistrictData
import com.unciv.civ6.domain.city.City
import com.unciv.civ6.domain.city.HexCoord
import com.unciv.civ6.domain.city.PlacedDistrict
import com.unciv.civ6.domain.government.Government
import com.unciv.civ6.domain.government.GovernmentManager
import com.unciv.civ6.domain.government.PolicyCard
import com.unciv.civ6.domain.government.PolicySlotType
import com.unciv.civ6.domain.tech.Civic
import com.unciv.civ6.domain.tech.CivicManager
import com.unciv.civ6.domain.tech.Tech
import com.unciv.civ6.domain.tech.TechManager
import com.unciv.civ6.engine.GameInfoV2

/**
 * Starter for Civ6 clean - creates GameInfoV2 with v2 managers, replaces GameStarter.kt Civ5.
 */
object Civ6GameStarter {

    fun newGame(): GameInfoV2 {
        val techs = mapOf(
            "Pottery" to Tech("Pottery", 25, 1, "Ancient", emptyList()),
            "Writing" to Tech("Writing", 35, 2, "Ancient", listOf("Pottery"), unlocks = listOf("Campus"))
        )
        val civics = mapOf(
            "Code of Laws" to Civic("Code of Laws", 20, 1, "Ancient", emptyList(), unlocksGovernment = "Chiefdom"),
            "Craftsmanship" to Civic("Craftsmanship", 25, 1, "Ancient", listOf("Code of Laws"))
        )
        val governments = mapOf(
            "Chiefdom" to Government("Chiefdom", "Ancient", mapOf(PolicySlotType.Military to 1, PolicySlotType.Economic to 1)),
            "Classical Republic" to Government("Classical Republic", "Classical", mapOf(PolicySlotType.Military to 1, PolicySlotType.Economic to 2, PolicySlotType.Diplomatic to 1, PolicySlotType.Wildcard to 1), requiredCivic = "Code of Laws")
        )
        val cards = mapOf(
            "Discipline" to PolicyCard("Discipline", PolicySlotType.Military),
            "Urban Planning" to PolicyCard("Urban Planning", PolicySlotType.Economic)
        )

        val gameInfo = GameInfoV2(
            techManager = TechManager(techs),
            civicManager = CivicManager(civics),
            governmentManager = GovernmentManager(governments, cards)
        )
        // Starter city
        val cap = City("Capital", 1, hasFreshWater = true)
        gameInfo.addCity(cap)
        gameInfo.governmentManager?.switchGovernment("Chiefdom", emptySet())
        return gameInfo
    }

    fun addDistrictToCity(gameInfo: GameInfoV2, cityName: String, district: DistrictData, at: HexCoord): Boolean {
        val city = gameInfo.cities.find { it.name == cityName } ?: return false
        if (!city.canBuildDistrict(district)) return false
        val placed = PlacedDistrict(district, at)
        // Replace city with updated districts (immutable copy)
        val idx = gameInfo.cities.indexOf(city)
        gameInfo.cities[idx] = city.copy(districts = city.districts + placed)
        return true
    }
}
