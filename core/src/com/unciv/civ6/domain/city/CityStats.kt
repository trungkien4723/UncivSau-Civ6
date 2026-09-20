package com.unciv.civ6.domain.city

import kotlin.math.ceil

/**
 * Civ6 Housing & Amenities - replaces Civ5 global happiness.
 * Based on CIV6_GAMEPLAY_MECHANICS.md #5
 */

// Housing: hard cap for population
object HousingCalculator {
    // Base housing from city center + water
    const val BASE_HOUSING_CITY_CENTER = 2
    const val BONUS_FRESH_WATER = 3 // river/lake settle
    const val BONUS_COASTAL_WATER = 1 // coast no fresh water
    const val AQUEDUCT_HOUSING = 6
    const val NEIGHBORHOOD_LOW_APPEAL = 1
    const val NEIGHBORHOOD_HIGH_APPEAL = 6

    fun calculateHousing(
        hasFreshWater: Boolean,
        hasCoastalWater: Boolean,
        buildingsHousing: Int,
        improvementsHousing: Int,
        neighborhoodsHousing: Int
    ): Int {
        var housing = BASE_HOUSING_CITY_CENTER
        housing += when {
            hasFreshWater -> BONUS_FRESH_WATER
            hasCoastalWater -> BONUS_COASTAL_WATER
            else -> 0
        }
        housing += buildingsHousing
        housing += improvementsHousing
        housing += neighborhoodsHousing
        return housing
    }

    fun growthModifier(population: Int, housing: Int): Float {
        val diff = housing - population
        return when {
            diff >= 2 -> 1f // no penalty
            diff == 1 -> 0.75f // -25% food growth when 1 under cap
            diff == 0 -> 0.5f // -50% when at cap
            else -> 0f // no growth when over cap
        }
    }
}

// Amenities: per-city happiness
object AmenityCalculator {
    /**
     * Formula: amenitiesNeeded = ceil(population/2) -1, but min 0.
     * Civ6: pop 1-2 needs 0, pop 3-4 needs 1, pop 5-6 needs 2, etc.
     */
    fun amenitiesNeeded(population: Int): Int {
        if (population <= 2) return 0
        return ceil(population / 2.0).toInt() - 1
    }

    enum class AmenityLevel(val bonusPercent: Int, val growthPenalty: Float) {
        Ecstatic(20, 0f), // +3 amenities
        Happy(10, 0f),    // +1 to +2
        Content(0, 0f),   // 0
        Unhappy(-5, 0.75f), // -1 to -2
        Unrest(-10, 0f),  // -3 to -4, no growth handled separately
        Revolt(-15, 0f)   // -5 or worse
    }

    fun level(amenities: Int, needed: Int): AmenityLevel {
        val diff = amenities - needed
        return when {
            diff >= 3 -> AmenityLevel.Ecstatic
            diff in 1..2 -> AmenityLevel.Happy
            diff == 0 -> AmenityLevel.Content
            diff == -1 || diff == -2 -> AmenityLevel.Unhappy
            diff == -3 || diff == -4 -> AmenityLevel.Unrest
            else -> AmenityLevel.Revolt
        }
    }

    /**
     * Luxury: 1 copy gives 1 amenity to 4 cities most in need. Duplicates give 0.
     * Entertainment Complex regional buildings cover 6 tiles.
     */
    fun amenitiesFromLuxuries(uniqueLuxuryCount: Int, citiesNeeding: Int): Int {
        // Simplified: each luxury covers up to 4 cities
        // For per-city calc, caller distributes using most-needy first.
        return uniqueLuxuryCount // caller will distribute 1 per luxury per 4 cities
    }
}

data class CityPopulation(
    val population: Int,
    val housing: Int,
    val amenities: Int,
    val housingGrowthModifier: Float = HousingCalculator.growthModifier(population, housing),
    val amenityLevel: AmenityCalculator.AmenityLevel = AmenityCalculator.level(amenities, AmenityCalculator.amenitiesNeeded(population))
)
