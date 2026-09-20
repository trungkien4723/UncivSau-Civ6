package com.unciv.civ6.achievements

/**
 * Achievements for Civ6v2
 */

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    var unlocked: Boolean = false
)

class AchievementManager(private val achievements: MutableList<Achievement> = mutableListOf(
    Achievement("first_city", "First City", "Found your first city"),
    Achievement("first_district", "District Placer", "Build your first district"),
    Achievement("first_wonder", "Wonder Builder", "Build a wonder"),
    Achievement("victory_science", "Science Victory", "Win a science victory")
)) {
    fun unlock(id: String): Boolean {
        val a = achievements.find { it.id == id } ?: return false
        if (a.unlocked) return false
        a.unlocked = true
        return true
    }

    fun unlocked(): List<Achievement> = achievements.filter { it.unlocked }
}
