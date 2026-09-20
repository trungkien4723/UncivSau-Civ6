package com.unciv.civ6.domain.combat

import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Civ6 battle damage - strength ratio determines damage.
 * Based on Civ6 combat formula (simplified): damage = 30 * exp(0.04 * strengthDiff)
 * Walls: attacker must deplete wallHp first before cityHp (Civ6: walls are HP barrier).
 */

object BattleConstants {
    const val BASE_DAMAGE = 30
    const val STRENGTH_FACTOR = 0.04
    const val MIN_DAMAGE = 0
    const val MAX_DAMAGE = 100
    const val SIEGE_VS_CITY_BONUS = 0 // Siege no penalty vs city (removed -17 for cities in fix 4.26.3)
    const val RANGED_VS_CITY_PENALTY = 0 // Removed -17 ranged vs city (fix 4.26.3)
}

object BattleDamage {

    data class DamageResult(
        val damageToDefender: Int,
        val damageToAttacker: Int, // retaliation if melee
        val wallDamage: Int = 0, // if defender is walled city, wall takes damage first
        val defenderKilled: Boolean = false,
        val attackerKilled: Boolean = false
    )

    /**
     * Calculate damage from attacker to defender.
     * defenderWallHp >0 means walls absorb first.
     */
    fun calculateDamage(attacker: Combatant, defender: Combatant, terrainModifier: Int = 0, supportBonus: Int = 0, flankingBonus: Int = 0): DamageResult {
        val attStrength = attacker.strengthAgainst(defender, terrainModifier, supportBonus, flankingBonus)
        val defStrength = defender.strengthAgainst(attacker, 0, 0, 0)
        val strengthDiff = attStrength - defStrength

        // Civ6 formula simplified
        val base = BattleConstants.BASE_DAMAGE * Math.E.pow(BattleConstants.STRENGTH_FACTOR * strengthDiff)
        var dmgToDef = base.roundToInt().coerceIn(BattleConstants.MIN_DAMAGE, BattleConstants.MAX_DAMAGE)

        // Ranged attackers don't take retaliation
        var dmgToAtt = 0
        if (!attacker.isRanged) {
            val retaliationBase = BattleConstants.BASE_DAMAGE * Math.E.pow(BattleConstants.STRENGTH_FACTOR * (-strengthDiff))
            dmgToAtt = retaliationBase.roundToInt().coerceIn(BattleConstants.MIN_DAMAGE, BattleConstants.MAX_DAMAGE)
            // Ranged defender retaliates only if not city and not ranged? Cities do ranged attack
            if (defender.isRanged && !attacker.isRanged) {
                // already accounted
            }
        }

        var wallDmg = 0
        var remainingDmg = dmgToDef
        if (defender is CityCombatant && defender.wallHp > 0) {
            wallDmg = minOf(remainingDmg, defender.wallHp)
            remainingDmg -= wallDmg
            // If walls still up, city HP not damaged beyond wall absorption? In Civ6 walls absorb all until gone
            // Simplified: wall takes first, remaining hits city
            dmgToDef = remainingDmg
        } else {
            wallDmg = 0
        }

        return DamageResult(
            damageToDefender = dmgToDef,
            damageToAttacker = dmgToAtt,
            wallDamage = wallDmg,
            defenderKilled = defender.currentHp - dmgToDef <= 0,
            attackerKilled = attacker.currentHp - dmgToAtt <= 0
        )
    }

    /**
     * Support bonus: +2 per adjacent friendly unit (Civ6 flanking/support)
     */
    fun supportBonus(adjacentFriendlies: Int): Int = adjacentFriendlies * 2

    /**
     * Flanking: +2 per flanking unit? Simplified.
     */
    fun flankingBonus(flankingUnits: Int): Int = flankingUnits * 2

    /**
     * Terrain: hills +3, woods +3, river crossing -? etc.
     */
    fun terrainModifier(isHill: Boolean, isWoods: Boolean, isFortified: Boolean): Int {
        var mod = 0
        if (isHill) mod += 3
        if (isWoods) mod += 3
        if (isFortified) mod += 6 // Fortify until healed
        return mod
    }
}
