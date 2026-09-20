package com.unciv.civ6.domain.city

/**
 * Civ6 adjacency types - core of district placement.
 * Each district checks surrounding 6 hexes + city center as district.
 */
enum class AdjacencyType {
    Mountain,       // +1 Science for Campus, +1 Faith Holy Site per mountain
    Rainforest,     // +0.5
    NaturalWonder,  // +2 Faith Holy Site
    Wonder,         // +1 Culture Theater
    River,          // +2 Gold Commercial Hub
    CoastalResource,// Harbor
    MineOrQuarry,   // Industrial
    District        // +0.5 for most (city center counts)
}

data class AdjacencyContext(
    val surroundingFeatures: Map<AdjacencyType, Int>, // count per type in 6 neighbors
    val adjacentDistrictCount: Int, // including city center
    val districtCountForBonus: Int // for +0.5 per district
)

object AdjacencyCalculator {
    /**
     * Calculate total adjacency yield for a district at placement.
     * Formula from CIV6_GAMEPLAY_MECHANICS.md #7
     */
    fun calculate(
        districtName: String,
        bonuses: List<com.unciv.civ6.data.AdjacencyBonus>,
        context: AdjacencyContext
    ): Float {
        var total = 0f
        for (bonus in bonuses) {
            val count = when (bonus.type) {
                AdjacencyType.District -> context.adjacentDistrictCount
                else -> context.surroundingFeatures[bonus.type] ?: 0
            }
            if (count == 0) continue
            // perCount allows +0.5 per 2 forests etc. Use float division
            total += (count / bonus.perCount.toFloat()) * bonus.amount
        }
        return total
    }

    /**
     * Policy card Scripture doubles Holy Site adjacency etc. Applied later via multiplier.
     */
    fun withPolicyMultiplier(baseAdjacency: Float, multiplier: Float = 1f): Float {
        return baseAdjacency * multiplier
    }
}
