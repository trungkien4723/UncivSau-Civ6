package com.unciv.civ6.domain.combat

/**
 * Combatant abstraction for Civ6 - units and cities.
 * Walls give HP only, not strength (fix 4.26.5).
 */

interface Combatant {
    val name: String
    val baseStrength: Int
    val isRanged: Boolean
    val isSiege: Boolean
    val isCity: Boolean
    val wallHp: Int // for cities only, otherwise 0
    val currentHp: Int
    val maxHp: Int
    fun strengthAgainst(other: Combatant, terrainModifier: Int = 0, supportBonus: Int = 0, flankingBonus: Int = 0): Int
}

data class UnitCombatant(
    override val name: String,
    override val baseStrength: Int,
    val rangedStrength: Int = 0,
    override val isRanged: Boolean = false,
    override val isSiege: Boolean = false,
    override val currentHp: Int = 100,
    override val maxHp: Int = 100,
    val promotions: List<String> = emptyList()
) : Combatant {
    override val isCity: Boolean = false
    override val wallHp: Int = 0
    override fun strengthAgainst(other: Combatant, terrainModifier: Int, supportBonus: Int, flankingBonus: Int): Int {
        var strength = if (isRanged && !other.isCity) rangedStrength else baseStrength
        // Civ6: Siege has -17 vs land units, but not vs cities
        if (isSiege && !other.isCity && other !is CityCombatant) strength -= 17
        // Siege is strong vs city walls (no penalty vs cities)
        strength += terrainModifier
        strength += supportBonus
        strength += flankingBonus
        return strength.coerceAtLeast(1)
    }
}

data class CityCombatant(
    override val name: String,
    override val baseStrength: Int, // city combat strength (from strongest unit + buildings, NOT walls)
    override val wallHp: Int = 0, // walls: 100 Ancient, 200 Medieval, 300 Renaissance (GS)
    override val currentHp: Int = 100,
    override val maxHp: Int = 100,
    val garrisonStrength: Int = 0
) : Combatant {
    override val isCity: Boolean = true
    override val isRanged: Boolean = false
    override val isSiege: Boolean = false
    override fun strengthAgainst(other: Combatant, terrainModifier: Int, supportBonus: Int, flankingBonus: Int): Int {
        // City attacks as ranged with baseStrength, walls don't add strength (Civ6 fix 4.26.5)
        return baseStrength + terrainModifier
    }

    fun isWalled(): Boolean = wallHp > 0
}
