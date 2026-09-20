package com.unciv.civ6.domain.city

import com.unciv.civ6.data.BuildingData
import com.unciv.civ6.data.DistrictData

/**
 * City v2 - Civ6 standard.
 * Districts are placed on map tiles, not stacked in city center.
 * 1 district type per city max, cost scales empire-wide.
 */
data class City(
    val name: String,
    val population: Int,
    val hasFreshWater: Boolean = false,
    val hasCoastalWater: Boolean = false,
    val districts: List<PlacedDistrict> = emptyList(),
    val buildings: List<PlacedBuilding> = emptyList(),
    val housingFromImprovements: Int = 0,
    val amenitiesFromLuxuries: Int = 0,
    val amenitiesFromEntertainment: Int = 0
) {
    // Civ6: district slots = 1 at pop1, +1 at 4,7,10,13,16,19...
    fun maxDistrictSlots(): Int {
        if (population < 1) return 0
        var slots = 1
        var threshold = 4
        while (population >= threshold) {
            slots += 1
            threshold += 3
        }
        return slots
    }

    fun canBuildDistrict(district: DistrictData): Boolean {
        if (districts.size >= maxDistrictSlots()) return false
        if (population < district.populationRequirement) return false
        if (districts.any { it.district.name == district.name }) return false // 1 per type per city
        return true
    }

    fun placedDistrictCost(district: DistrictData, empireBuiltCount: Int): Int {
        // Civ6 scaling: cost increases per district built empire-wide (simplified +15 per count)
        return district.cost + empireBuiltCount * 15
    }

    fun housing(): Int {
        val buildingHousing = buildings.sumOf { it.building.housing }
        val districtHousing = districts.sumOf { 0 } // Neighborhood handled via improvement
        return HousingCalculator.calculateHousing(
            hasFreshWater, hasCoastalWater,
            buildingHousing, housingFromImprovements, districtHousing
        )
    }

    fun amenities(): Int = amenitiesFromLuxuries + amenitiesFromEntertainment

    fun populationState(): CityPopulation = CityPopulation(population, housing(), amenities())

    fun adjacencyForNewDistrict(district: DistrictData, context: AdjacencyContext): Float {
        return AdjacencyCalculator.calculate(district.name, district.adjacencyBonuses, context)
    }
}

data class PlacedDistrict(
    val district: DistrictData,
    val location: HexCoord,
    val adjacencyYield: Float = 0f,
    val buildings: List<PlacedBuilding> = emptyList()
) {
    fun canAddBuilding(building: BuildingData): Boolean {
        if (building.requiredDistrict != district.name) return false
        if (buildings.size >= 3) return false // max 3 per district
        if (buildings.any { it.building.name == building.name }) return false
        return true
    }
}

data class PlacedBuilding(
    val building: BuildingData,
    val districtName: String
)

data class HexCoord(val q: Int, val r: Int) {
    fun neighbors(): List<HexCoord> = listOf(
        HexCoord(q+1, r), HexCoord(q-1, r),
        HexCoord(q, r+1), HexCoord(q, r-1),
        HexCoord(q+1, r-1), HexCoord(q-1, r+1)
    )
}
