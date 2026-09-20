package com.unciv.civ6.ui

import com.unciv.civ6.domain.city.City
import com.unciv.civ6.domain.city.AdjacencyContext

/**
 * Civ6 CityScreen adapter - shows district placement, adjacency preview, housing/amenity.
 * Replaces CityScreen.kt Civ5 (building queue stacking).
 */
class Civ6CityScreen(private val city: City) {

    fun housingStatus(): String {
        val pop = city.populationState()
        return "Housing ${city.housing()} vs Pop ${city.population} Growth x${pop.housingGrowthModifier} ${if (city.housing() < city.population) "⚠ Overcrowded" else ""}"
    }

    fun amenityStatus(): String {
        val pop = city.populationState()
        return "Amenities ${city.amenities()} Needed ${pop.amenityLevel} Bonus ${pop.amenityLevel.bonusPercent}%"
    }

    fun districtSlots(): String {
        return "Districts ${city.districts.size}/${city.maxDistrictSlots()} (Pop ${city.population})"
    }

    fun adjacencyPreview(districtName: String, context: AdjacencyContext): String {
        val district = city.districts.find { it.district.name == districtName }?.district
            ?: return "District $districtName not found"
        val adj = city.adjacencyForNewDistrict(district, context)
        return "Adjacency for $districtName: $adj"
    }

    fun canBuildDistrict(districtName: String): Boolean {
        val dummy = city.districts.find { it.district.name == districtName }?.district ?: return false
        return city.canBuildDistrict(dummy)
    }
}
