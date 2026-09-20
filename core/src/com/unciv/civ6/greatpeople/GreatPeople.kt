package com.unciv.civ6.greatpeople

/**
 * Great People for Civ6v2
 */

enum class GreatPersonType { Scientist, Engineer, Merchant, General, Admiral, Prophet, Writer, Artist, Musician }

data class GreatPerson(
    val name: String,
    val type: GreatPersonType,
    val era: String,
    val ability: String,
    var recruited: Boolean = false
)

class GreatPeopleManager(private val pool: MutableList<GreatPerson> = mutableListOf(
    GreatPerson("Einstein", GreatPersonType.Scientist, "Modern", "+1 Science per Campus"),
    GreatPerson("Newton", GreatPersonType.Scientist, "Renaissance", "Boosts tech"),
    GreatPerson("Michelangelo", GreatPersonType.Artist, "Renaissance", "Great Work")
)) {
    fun available(type: GreatPersonType): List<GreatPerson> = pool.filter { it.type == type && !it.recruited }
    fun recruit(name: String): Boolean {
        val p = pool.find { it.name == name && !it.recruited } ?: return false
        p.recruited = true
        return true
    }
    fun pointsNeeded(type: GreatPersonType): Int = 60 // simplified
}
