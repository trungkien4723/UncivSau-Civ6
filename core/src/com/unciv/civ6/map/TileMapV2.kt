package com.unciv.civ6.map

import com.unciv.civ6.domain.city.HexCoord

/**
 * TileMap v2 for Civ6 - districts occupy tiles, not city center stacking.
 * Each tile can hold 1 district OR improvement, not both. Resource lost if district placed.
 */

enum class TerrainType { Grassland, Plains, Desert, Tundra, Snow, Hills, Mountain, Coast, Ocean, Lake }
enum class TerrainFeature { None, Woods, Rainforest, Marsh, Floodplains, Oasis }
enum class ResourceType { None, Bonus, Luxury, Strategic }

data class TileV2(
    val coord: HexCoord,
    var terrain: TerrainType = TerrainType.Grassland,
    var feature: TerrainFeature = TerrainFeature.None,
    var resource: String? = null, // luxury/strategic name
    var resourceType: ResourceType = ResourceType.None,
    var district: String? = null, // district name if placed
    var improvement: String? = null,
    var isCityCenter: Boolean = false,
    var ownerCity: String? = null
) {
    fun canPlaceDistrict(districtName: String, terrainRequirement: Set<String>): Boolean {
        if (district != null || improvement != null) return false
        if (isCityCenter) return false
        if (terrainRequirement.isNotEmpty()) {
            val terrainName = terrain.name
            if (terrainName !in terrainRequirement && feature.name !in terrainRequirement) return false
        }
        // Civ6: district on resource loses resource
        return true
    }

    fun placeDistrict(districtName: String) {
        // Lose resource if present (Civ6 rule)
        if (resource != null) {
            resource = null
            resourceType = ResourceType.None
        }
        district = districtName
    }

    fun appeal(): Int {
        // Simplified: woods +1, rainforest +1, mountain +2, etc.
        var a = 0
        if (feature == TerrainFeature.Woods) a += 1
        if (feature == TerrainFeature.Rainforest) a += 1
        if (terrain == TerrainType.Mountain) a += 2
        return a
    }
}

class TileMapV2(val width: Int, val height: Int) {
    private val tiles = mutableMapOf<HexCoord, TileV2>()

    init {
        for (q in 0 until width) for (r in 0 until height) {
            tiles[HexCoord(q, r)] = TileV2(HexCoord(q, r))
        }
    }

    fun get(coord: HexCoord): TileV2? = tiles[coord]
    fun getNeighbors(coord: HexCoord): List<TileV2> = coord.neighbors().mapNotNull { tiles[it] }
    fun allTiles(): Collection<TileV2> = tiles.values

    fun placeDistrict(coord: HexCoord, districtName: String, terrainReq: Set<String> = emptySet()): Boolean {
        val tile = get(coord) ?: return false
        if (!tile.canPlaceDistrict(districtName, terrainReq)) return false
        tile.placeDistrict(districtName)
        return true
    }

    fun cityTiles(cityCenter: HexCoord, radius: Int = 3): List<TileV2> {
        // Return tiles within radius 3 (Civ6 city workable radius)
        return tiles.values.filter { hexDistance(it.coord, cityCenter) <= radius }
    }

    private fun hexDistance(a: HexCoord, b: HexCoord): Int {
        return (kotlin.math.abs(a.q - b.q) + kotlin.math.abs(a.q + a.r - b.q - b.r) + kotlin.math.abs(a.r - b.r)) / 2
    }
}

data class MapParametersV2(
    val width: Int = 40,
    val height: Int = 40,
    val mapType: String = "Continents",
    val seed: Long = System.currentTimeMillis()
)
