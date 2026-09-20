package com.unciv.civ6.domain.city

import com.unciv.civ6.data.BuildingData
import com.unciv.civ6.data.DistrictData

/**
 * Construction queue for Civ6 - districts on map, not city queue stacking.
 */
class CityConstructions(val city: City) {
    private val districtQueue = mutableListOf<DistrictData>()
    private val buildingQueue = mutableListOf<BuildingData>()

    fun enqueueDistrict(district: DistrictData, empireBuiltCount: Int): Result<Int> {
        if (!city.canBuildDistrict(district)) return Result.failure(IllegalStateException("Cannot build ${district.name}: pop ${city.population} slots ${city.maxDistrictSlots()}"))
        val cost = city.placedDistrictCost(district, empireBuiltCount)
        districtQueue.add(district)
        return Result.success(cost)
    }

    fun enqueueBuilding(building: BuildingData, districtName: String): Result<Int> {
        val district = city.districts.find { it.district.name == districtName }
            ?: return Result.failure(IllegalStateException("District $districtName not built"))
        if (!district.canAddBuilding(building)) return Result.failure(IllegalStateException("Cannot add ${building.name} to ${districtName}"))
        buildingQueue.add(building)
        return Result.success(building.cost)
    }

    fun queue(): List<String> = districtQueue.map { it.name } + buildingQueue.map { it.name }
}
