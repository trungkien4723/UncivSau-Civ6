package com.unciv.civ6.domain.victory

/**
 * 6 Victory conditions Civ6 - CIV6_GAMEPLAY_MECHANICS.md #23
 */

enum class VictoryType { Domination, Science, Culture, Religion, Diplomacy, Score }

data class VictoryProgress(
    val type: VictoryType,
    var progress: Int = 0,
    var required: Int = 100,
    var isAchieved: Boolean = false
)

class VictoryManager(
    private val progressMap: MutableMap<VictoryType, VictoryProgress> = mutableMapOf(
        VictoryType.Domination to VictoryProgress(VictoryType.Domination, required = 1), // capitals needed
        VictoryType.Science to VictoryProgress(VictoryType.Science, required = 5), // space projects: satellite, moon, mars x3
        VictoryType.Culture to VictoryProgress(VictoryType.Culture, required = 100), // tourism
        VictoryType.Religion to VictoryProgress(VictoryType.Religion, required = 1), // all civs converted
        VictoryType.Diplomacy to VictoryProgress(VictoryType.Diplomacy, required = 10), // points
        VictoryType.Score to VictoryProgress(VictoryType.Score, required = 500) // score at time limit
    )
) {
    fun updateDomination(capitalsOwned: Int, totalCapitals: Int): Boolean {
        val p = progressMap[VictoryType.Domination]!!
        p.progress = capitalsOwned
        p.required = totalCapitals
        p.isAchieved = capitalsOwned >= totalCapitals
        return p.isAchieved
    }

    fun addScienceProject(): Boolean {
        val p = progressMap[VictoryType.Science]!!
        p.progress++
        p.isAchieved = p.progress >= p.required
        return p.isAchieved
    }

    fun updateCulture(foreignTourists: Int, domesticTouristsNeeded: Int): Boolean {
        val p = progressMap[VictoryType.Culture]!!
        p.progress = foreignTourists
        p.required = domesticTouristsNeeded
        p.isAchieved = foreignTourists >= domesticTouristsNeeded
        return p.isAchieved
    }

    fun updateReligion(civsConverted: Int, totalCivs: Int): Boolean {
        val p = progressMap[VictoryType.Religion]!!
        p.progress = civsConverted
        p.required = totalCivs
        p.isAchieved = civsConverted >= totalCivs
        return p.isAchieved
    }

    fun updateDiplomacy(points: Int): Boolean {
        val p = progressMap[VictoryType.Diplomacy]!!
        p.progress = points
        p.isAchieved = points >= 10
        return p.isAchieved
    }

    fun winner(): VictoryType? = progressMap.values.find { it.isAchieved }?.type

    fun allProgress(): Map<VictoryType, VictoryProgress> = progressMap.toMap()
}
