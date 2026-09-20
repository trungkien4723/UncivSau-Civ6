package com.unciv.civ6.domain.loyalty

/**
 * Civ6 Loyalty + Era Score - Rise & Fall.
 * Loyalty 0-100, <0 -> Free City flip.
 */

enum class EraType { Ancient, Classical, Medieval, Renaissance, Industrial, Modern, Atomic, Information }

enum class AgeType { DarkAge, NormalAge, GoldenAge, HeroicAge } // Heroic = Dark -> Golden

data class LoyaltyPressure(
    val fromCityPopulation: Int,
    val distance: Int, // hex distance to source city
    val isGoldenAge: Boolean = false,
    val hasGovernor: Boolean = false
) {
    // Simplified Civ6 pressure: pop * (10 - distance)/10 + bonuses
    fun pressure(): Float {
        var p = fromCityPopulation * (10 - distance).coerceAtLeast(0) / 10f
        if (isGoldenAge) p *= 1.5f
        if (hasGovernor) p += 8 // governor +8 loyalty stability
        return p
    }
}

data class CityLoyalty(
    val cityName: String,
    var loyalty: Float = 100f, // 0-100
    val population: Int,
    val isCapital: Boolean = false
) {
    fun update(ownPressure: Float, foreignPressure: Float, amenitiesModifier: Int = 0, hasGovernor: Boolean = false): Float {
        if (isCapital) return 100f // capital always 100
        val net = ownPressure - foreignPressure + amenitiesModifier + (if (hasGovernor) 8 else 0)
        loyalty = (loyalty + net * 0.1f).coerceIn(0f, 100f)
        return loyalty
    }

    fun isFreeCity(): Boolean = loyalty <= 0
    fun isRevoltRisk(): Boolean = loyalty < 25
}

object EraScoreManager {
    // Thresholds for Standard speed
    private val thresholds = mapOf(
        EraType.Ancient to 10,
        EraType.Classical to 20,
        EraType.Medieval to 32,
        EraType.Renaissance to 44
    )

    fun ageType(era: EraType, score: Int, previousWasDark: Boolean): AgeType {
        val threshold = thresholds[era] ?: 30
        return when {
            score >= threshold + 12 && previousWasDark -> AgeType.HeroicAge
            score >= threshold + 5 -> AgeType.GoldenAge
            score >= threshold - 5 -> AgeType.NormalAge
            else -> AgeType.DarkAge
        }
    }

    // Historic moments: wonder, district first, natural wonder, etc.
    fun pointsForWonder(isFirstOfEra: Boolean): Int = if (isFirstOfEra) 4 else 2
    fun pointsForDistrict(isFirst: Boolean): Int = if (isFirst) 3 else 1
    fun pointsForNaturalWonder(): Int = 3
}
