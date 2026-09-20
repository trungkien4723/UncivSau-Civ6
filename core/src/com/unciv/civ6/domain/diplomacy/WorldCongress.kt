package com.unciv.civ6.domain.diplomacy

/**
 * World Congress + Diplomatic Victory (GS).
 * Diplomatic Favor is currency, 10 points to win.
 */

data class Resolution(
    val name: String,
    val description: String,
    val options: List<String> // [A vs B]
)

class WorldCongress(
    var turnCounter: Int = 0,
    var favorPerTurn: Int = 0,
    var diplomaticPoints: Int = 0,
    private val resolutions: List<Resolution> = emptyList()
) {
    fun nextSession(): List<Resolution> {
        turnCounter += 30
        return resolutions.shuffled().take(2)
    }

    fun addFavor(amount: Int) { favorPerTurn += amount }
    fun spendFavor(amount: Int): Boolean {
        if (favorPerTurn < amount) return false
        favorPerTurn -= amount
        return true
    }

    fun voteForPoints(favorSpent: Int): Int {
        // Simplified: 30 favor = 1 point, need 10 points
        val points = favorSpent / 30
        diplomaticPoints += points
        favorPerTurn -= favorSpent
        return points
    }

    fun hasWon(): Boolean = diplomaticPoints >= 10

    fun scoredCompetitionWinner(participants: Map<String, Int>): String? {
        // Highest score wins, gets 1 point
        return participants.maxByOrNull { it.value }?.key
    }
}
