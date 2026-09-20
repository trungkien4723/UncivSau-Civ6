package com.unciv.civ6.balancing

/**
 * Balancing config for Civ6 clean - centralizes tuning numbers, replaces scattered Civ5 constants.
 * All yields, costs, and modifiers here for easy QA.
 */

object Balancing {
    // City
    const val DISTRICT_COST_BASE = 60
    const val DISTRICT_COST_SCALING = 15 // per empire district
    const val DISTRICT_SLOTS_POP_THRESHOLDS = 3 // 1,4,7,10...

    // Housing
    const val HOUSING_BASE = 2
    const val HOUSING_FRESH_WATER = 3
    const val HOUSING_COASTAL = 1
    const val HOUSING_AQUEDUCT = 6

    // Amenities
    const val AMENITIES_PER_LUXURY = 1 // 1 per 4 cities
    const val AMENITIES_CITIES_PER_LUXURY = 4

    // Tech/Civic
    const val EUREKA_BOOST_PERCENT = 50
    const val INSPIRATION_BOOST_PERCENT = 50

    // Combat
    const val BASE_DAMAGE = 30
    const val STRENGTH_FACTOR = 0.04
    const val SIEGE_VS_LAND_PENALTY = -17
    const val SUPPORT_BONUS = 2
    const val FLANKING_BONUS = 2
    const val TERRAIN_HILL = 3
    const val TERRAIN_FORTIFIED = 6

    // Loyalty
    const val LOYALTY_GOLDEN_AGE_MULT = 1.5f
    const val LOYALTY_GOVERNOR = 8f
    const val LOYALTY_CAPITAL = 100f

    // Religion
    const val FAITH_PANTHEON = 25
    const val PROPHET_POINTS = 60

    // Era
    const val ERA_THRESHOLD_ANCIENT = 10

    // Victory
    const val DIPLOMATIC_POINTS_NEEDED = 10
    const val SCIENCE_PROJECTS_NEEDED = 5

    // Climate
    const val CO2_COAL = 3.28f
    const val CO2_OIL = 1.96f

    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        if (DISTRICT_COST_SCALING < 0) errors.add("DISTRICT_COST_SCALING negative")
        if (EUREKA_BOOST_PERCENT != 50) errors.add("EUREKA must be 50% Civ6")
        if (SIEGE_VS_LAND_PENALTY != -17) errors.add("Siege penalty must be -17")
        return errors
    }
}
