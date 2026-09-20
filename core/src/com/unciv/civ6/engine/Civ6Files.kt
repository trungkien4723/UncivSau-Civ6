package com.unciv.civ6.engine

import com.badlogic.gdx.files.FileHandle
import com.unciv.json.json
import com.unciv.civ6.map.TileMapV2

/**
 * Civ6Files - Save/Load v10, replaces UncivFiles.kt Civ5 (which had v4 + misleading incompatible error).
 * Uses Civ6CompatibilityVersion.CURRENT_NUMBER=10, JSON plain (not Gzip for debuggability in rewrite).
 */

class Civ6Files(
    private val saveDir: String = "SaveFilesCiv6"
) {
    fun gameInfoToString(gameInfo: GameInfoV2): String {
        gameInfo.version = Civ6CompatibilityVersion.CURRENT
        return json().toJson(gameInfo)
    }

    fun gameInfoFromString(jsonStr: String): GameInfoV2 {
        val info = try {
            json().fromJson(GameInfoV2::class.java, jsonStr)
        } catch (ex: Exception) {
            // Try parse only version to give accurate error (like fix in master for v4)
            try {
                val onlyVersion = json().fromJson(Civ6VersionWrapper::class.java, jsonStr)
                if (onlyVersion.version.number > Civ6CompatibilityVersion.CURRENT_NUMBER) {
                    throw IncompatibleCiv6VersionException(onlyVersion.version, ex)
                }
            } catch (_: Exception) {}
            throw ex
        } ?: throw Exception("Corrupted save: null GameInfoV2")
        if (info.version.number > Civ6CompatibilityVersion.CURRENT_NUMBER) {
            throw IncompatibleCiv6VersionException(info.version)
        }
        return info
    }

    fun saveGame(gameInfo: GameInfoV2, name: String, file: FileHandle) {
        file.writeString(gameInfoToString(gameInfo), false)
    }

    fun loadGame(file: FileHandle): GameInfoV2 {
        val str = file.readString()
        if (str.isBlank()) throw Exception("Empty save file ${file.name()}")
        return gameInfoFromString(str)
    }

    // Wrapper to parse only version field
    private data class Civ6VersionWrapper(val version: Civ6CompatibilityVersion = Civ6CompatibilityVersion.CURRENT)
}

class IncompatibleCiv6VersionException(
    val version: Civ6CompatibilityVersion,
    cause: Throwable? = null
) : Exception("Save was created with incompatible Civ6 version: ${version.createdWith.text} (Build ${version.createdWith.number}). Please update to this version or later.", cause)

class Civ6Autosaves(private val files: Civ6Files) {
    fun autoSave(gameInfo: GameInfoV2, file: FileHandle) {
        files.saveGame(gameInfo, "Autosave", file)
    }
}
