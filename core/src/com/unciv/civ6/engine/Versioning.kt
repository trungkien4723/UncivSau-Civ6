package com.unciv.civ6.engine

/**
 * Civ6 v2 Versioning - bump từ 4 (Civ5) lên 10 để cắt save cũ, không migrate.
 * Tương đương core/src/com/unciv/logic/Versioning.kt:44 nhưng riêng cho civ6-rewrite.
 */
data class Civ6Version(val text: String, val number: Int) {
    fun toNiceString(): String = "[$text] (Build [$number])"
}

data class Civ6CompatibilityVersion(val number: Int, val createdWith: Civ6Version) : Comparable<Civ6CompatibilityVersion> {
    override fun compareTo(other: Civ6CompatibilityVersion): Int = number.compareTo(other.number)
    companion object {
        const val CURRENT_NUMBER = 10
        val CURRENT = Civ6CompatibilityVersion(CURRENT_NUMBER, Civ6Version("4.26.14-civ6", 2000))
        val FIRST_CIV6 = Civ6CompatibilityVersion(10, Civ6Version("4.26.14-civ6", 2000))
    }
}
