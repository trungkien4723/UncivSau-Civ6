package com.unciv.civ6.domain.religion

/**
 * Religion - Pantheon -> Great Prophet -> Found -> Evangelize.
 */

enum class BeliefType { Pantheon, Follower, Founder, Enhancer, Worship }

data class Belief(
    val name: String,
    val type: BeliefType,
    val description: String = "",
    val bonus: Map<String, Int> = emptyMap()
)

data class Religion(
    val name: String,
    val beliefs: List<Belief>, // max 4 + pantheon
    val pantheon: Belief? = null
) {
    init {
        require(beliefs.size <= 4) { "Max 4 beliefs + pantheon" }
        require(beliefs.none { it.type == BeliefType.Pantheon }) { "Pantheon separate" }
    }
}

class ReligionManager(
    private val pantheons: List<Belief>,
    private val availableBeliefs: MutableList<Belief> = mutableListOf(),
    private var faith: Int = 0,
    private var greatProphetPoints: Int = 0
) {
    var pantheon: Belief? = null
        private set
    var religion: Religion? = null
        private set

    fun addFaith(amount: Int) { faith += amount }
    fun addProphetPoints(amount: Int) { greatProphetPoints += amount }

    fun canFoundPantheon(): Boolean = pantheon == null && faith >= 25

    fun foundPantheon(beliefName: String): Boolean {
        if (!canFoundPantheon()) return false
        val belief = pantheons.find { it.name == beliefName } ?: return false
        pantheon = belief
        faith -= 25
        return true
    }

    fun canFoundReligion(): Boolean = pantheon != null && religion == null && greatProphetPoints >= 60

    fun foundReligion(religionName: String, followerBelief: String, extraBelief: String): Boolean {
        if (!canFoundReligion()) return false
        val follower = availableBeliefs.find { it.name == followerBelief && it.type == BeliefType.Follower } ?: return false
        val extra = availableBeliefs.find { it.name == extraBelief } ?: return false
        // beliefs cannot be reused globally - caller ensures uniqueness
        religion = Religion(religionName, listOf(follower, extra), pantheon)
        availableBeliefs.remove(follower)
        availableBeliefs.remove(extra)
        greatProphetPoints -= 60
        return true
    }

    fun evangelize(newBeliefName: String, apostleCharges: Int): Boolean {
        if (apostleCharges < 3) return false
        val current = religion ?: return false
        if (current.beliefs.size >= 4) return false
        val belief = availableBeliefs.find { it.name == newBeliefName } ?: return false
        religion = current.copy(beliefs = current.beliefs + belief)
        availableBeliefs.remove(belief)
        return true
    }

    fun greatProphetCost(): Int = 60
}
