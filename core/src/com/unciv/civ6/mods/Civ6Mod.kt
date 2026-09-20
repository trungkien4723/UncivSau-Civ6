package com.unciv.civ6.mods

/**
 * Mod system for Civ6v2 - replaces Mods for Civ5.
 * Each mod is a folder under mods/ with Civ6v2 JSONs, loaded via Civ6ModLoader.
 */

data class Civ6Mod(
    val name: String,
    val version: String = "1.0",
    val author: String = "",
    val description: String = "",
    val dependencies: List<String> = emptyList(),
    val jsons: Map<String, String> = emptyMap() // filename -> json content
)

class Civ6ModLoader(
    private val baseJsons: Map<String, String>, // base Civ6v2
    private val mods: List<Civ6Mod>
) {
    fun loadedJsons(): Map<String, String> {
        val merged = baseJsons.toMutableMap()
        for (mod in mods.sortedBy { it.dependencies.size }) {
            for ((file, content) in mod.jsons) {
                // Simple merge: mod overrides base file completely (Civ6 style, not patch)
                merged[file] = content
            }
        }
        return merged
    }

    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        for (mod in mods) {
            for (dep in mod.dependencies) {
                if (mods.none { it.name == dep } && dep != "Civ6v2") errors.add("Mod ${mod.name} missing dependency $dep")
            }
        }
        return errors
    }

    fun workshopUrl(modName: String): String {
        // R2 workshop: https://your-worker.workers.dev/mods/:modName.zip
        return "https://unciv-civ6.workers.dev/mods/$modName.zip"
    }
}

class Civ6Workshop(
    private val r2Bucket: String = "unciv-civ6-mods"
) {
    fun publishUrl(mod: Civ6Mod): String = "https://$r2Bucket.workers.dev/mods/${mod.name}.zip"
    fun downloadUrl(modName: String): String = "https://$r2Bucket.workers.dev/mods/$modName.zip"
}
