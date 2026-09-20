package com.unciv.civ6.data

import com.unciv.civ6.domain.city.AdjacencyType

/**
 * District v2 - Civ6 standard, no Civ5 stacking.
 * Cost scaling: baseCost + perDistrictBuilt * scaling (Civ6: + scaling per built district empire-wide)
 */
data class DistrictData(
    val name: String,
    val cost: Int,
    val maintenance: Int = 0,
    val populationRequirement: Int = 1, // 1,4,7,10,13...
    val terrainRequirement: Set<String> = emptySet(), // e.g. "Coast" for Harbor, "River" for Aqueduct adjacency
    val specialistSlots: Int = 0,
    val requiresAdjacentCityCenter: Boolean = false,
    val maxPerCity: Int = 1,
    val adjacencyBonuses: List<AdjacencyBonus> = emptyList(),
    val description: String = ""
)

data class AdjacencyBonus(
    val type: AdjacencyType,
    val yieldType: String, // Science, Faith, Gold, Production, Culture
    val amount: Float,
    val perCount: Int = 1, // +amount per N count (e.g. +0.5 per district)
    val requiredAdjacentFeature: String? = null // Mountain, Rainforest, Wonder, River, Mine, etc.
)

data class BuildingData(
    val name: String,
    val cost: Int,
    val maintenance: Int = 0,
    val requiredDistrict: String, // must be in district, not city center Civ5 style
    val requiredPopulation: Int = 1,
    val requiredTech: String? = null,
    val requiredCivic: String? = null,
    val yields: Map<String, Int> = emptyMap(), // yieldType -> amount
    val housing: Int = 0,
    val amenities: Int = 0,
    val specialistSlots: Int = 0,
    val isRegional: Boolean = false // e.g. Factory, Zoo affects 6 tiles
)

data class TerrainFeature(
    val name: String,
    val yields: Map<String, Int> = emptyMap()
)
