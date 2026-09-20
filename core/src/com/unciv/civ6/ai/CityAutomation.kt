package com.unciv.civ6.ai

import com.unciv.civ6.data.DistrictData
import com.unciv.civ6.domain.city.City
import com.unciv.civ6.domain.city.HexCoord
import com.unciv.civ6.map.TileMapV2

/**
 * CityAutomation - Civ6 AI for district placement.
 * Chooses best district based on adjacency + victory path.
 */
object CityAutomation {
    enum class VictoryFocus { Science, Culture, Gold, Production }

    fun chooseDistrict(city: City, available: List<DistrictData>, focus: VictoryFocus): DistrictData? {
        if (available.isEmpty()) return null
        if (!city.canBuildDistrict(available.first())) return null
        // Priority by focus
        val priority = when (focus) {
            VictoryFocus.Science -> listOf("Campus", "Industrial Zone", "Commercial Hub")
            VictoryFocus.Culture -> listOf("Theater Square", "Campus", "Holy Site")
            VictoryFocus.Gold -> listOf("Commercial Hub", "Harbor", "Industrial Zone")
            VictoryFocus.Production -> listOf("Industrial Zone", "Campus", "Commercial Hub")
        }
        for (p in priority) {
            val d = available.find { it.name == p }
            if (d != null && city.canBuildDistrict(d)) return d
        }
        return available.firstOrNull { city.canBuildDistrict(it) }
    }

    fun bestPlacement(cityCenter: HexCoord, district: DistrictData, map: TileMapV2): HexCoord? {
        val candidates = map.cityTiles(cityCenter, 3).filter { it.district == null && !it.isCityCenter }
        var best: HexCoord? = null
        var bestAdj = -1f
        for (tile in candidates) {
            if (!tile.canPlaceDistrict(district.name, district.terrainRequirement)) continue
            // Simplified adjacency score: count mountains/rivers nearby
            var adj = 0f
            for (n in map.getNeighbors(tile.coord)) {
                if (n.terrain.name == "Mountain") adj += 1
                if (n.district != null) adj += 0.5f
            }
            if (adj > bestAdj) {
                bestAdj = adj
                best = tile.coord
            }
        }
        return best
    }
}
