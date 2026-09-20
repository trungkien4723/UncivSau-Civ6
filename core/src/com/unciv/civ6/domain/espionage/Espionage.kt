package com.unciv.civ6.domain.espionage

/**
 * Espionage Civ6 - Spies with missions, counterspy, diplomatic visibility.
 */

enum class SpyRank { Rookie, Agent, Operative, SpecialAgent }
enum class SpyMission { StealTech, StealGold, RecruitPartisans, FomentUnrest, NeutralizeGovernor, Counterspy }

data class Spy(
    val name: String,
    var rank: SpyRank = SpyRank.Rookie,
    var locationCity: String? = null,
    var mission: SpyMission? = null,
    var turnsRemaining: Int = 0
) {
    fun startMission(city: String, mission: SpyMission, visibility: Int): Boolean {
        if (locationCity != null && turnsRemaining > 0) return false
        locationCity = city
        this.mission = mission
        turnsRemaining = missionDuration(mission, visibility)
        return true
    }

    private fun missionDuration(mission: SpyMission, visibility: Int): Int {
        var base = when (mission) {
            SpyMission.StealTech -> 8
            SpyMission.StealGold -> 6
            SpyMission.RecruitPartisans -> 8
            SpyMission.FomentUnrest -> 6
            SpyMission.NeutralizeGovernor -> 6
            SpyMission.Counterspy -> 10
        }
        // Higher visibility reduces duration
        base -= (visibility / 2)
        return base.coerceAtLeast(3)
    }

    fun tick(): Boolean {
        if (turnsRemaining > 0) turnsRemaining--
        if (turnsRemaining == 0 && mission != null) {
            // Promotion on success
            if (rank != SpyRank.SpecialAgent) rank = SpyRank.values()[rank.ordinal + 1]
            return true // mission completed
        }
        return false
    }

    fun successChance(visibility: Int): Int {
        var chance = 70 + rank.ordinal * 5
        chance -= visibility * 2
        return chance.coerceIn(10, 95)
    }
}

class EspionageManager(private val spies: MutableList<Spy> = mutableListOf()) {
    fun addSpy(spy: Spy) { spies.add(spy) }
    fun active(): List<Spy> = spies.filter { it.turnsRemaining > 0 }
    fun counterspies(): List<Spy> = spies.filter { it.mission == SpyMission.Counterspy }
    fun tickAll(): List<Spy> {
        val completed = mutableListOf<Spy>()
        for (spy in spies) if (spy.tick()) completed.add(spy)
        return completed
    }
}
