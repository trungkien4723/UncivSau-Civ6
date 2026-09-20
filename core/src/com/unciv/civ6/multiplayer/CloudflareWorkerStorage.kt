package com.unciv.civ6.multiplayer

/**
 * Cloudflare Worker storage for Civ6 multiplayer - like flagfish/horse pattern.
 * Replaces UncivServerFileStorage.kt / Dropbox for civ6-rewrite.
 * Worker uses KV (or D1) for game JSON v10 + preview, R2 for backup.
 * Endpoint: https://<worker>.workers.dev
 *
 * API:
 * PUT /game/:gameId { gameInfoV2 JSON, preview } -> save (with If-Match checksum)
 * GET /game/:gameId -> JSON
 * GET /games?playerId=xxx -> list previews
 * DELETE /game/:gameId
 */

interface Civ6FileStorage {
    suspend fun saveGame(gameId: String, json: String, previewJson: String? = null): Boolean
    suspend fun loadGame(gameId: String): String?
    suspend fun listGames(playerId: String): List<String>
    suspend fun deleteGame(gameId: String): Boolean
}

class CloudflareWorkerStorage(
    private val workerUrl: String, // e.g. https://unciv-civ6.workers.dev
    private val authToken: String? = null // Bearer token or secret header
) : Civ6FileStorage {

    private fun authHeaders(): Map<String, String> {
        return if (authToken != null) mapOf("Authorization" to "Bearer $authToken") else emptyMap()
    }

    override suspend fun saveGame(gameId: String, json: String, previewJson: String?): Boolean {
        // Real impl uses Ktor client: PUT $workerUrl/game/$gameId
        // Headers: Authorization, If-Match (checksum), Content-Type: application/json
        // Body: { "game": json, "preview": previewJson }
        // Return true if 200, false if 409 conflict (need to reload)
        return true // placeholder for scaffold - actual Ktor call in Sprint13 integration
    }

    override suspend fun loadGame(gameId: String): String? {
        // GET $workerUrl/game/$gameId
        return null // placeholder
    }

    override suspend fun listGames(playerId: String): List<String> {
        // GET $workerUrl/games?playerId=$playerId
        return emptyList()
    }

    override suspend fun deleteGame(gameId: String): Boolean {
        // DELETE $workerUrl/game/$gameId
        return true
    }

    companion object {
        const val WORKER_URL_PLACEHOLDER = "https://unciv-civ6.your-subdomain.workers.dev"
        // Flagfish/horse pattern: Worker KV namespace binding = "GAMES", R2 bucket = "backups"
    }
}

/**
 * Horse-like Durable Object for realtime turn notification (optional WebSocket).
 * If using Durable Object, gameId maps to DO id, clients WebSocket to /ws/:gameId.
 */
class CloudflareRealtime {
    // Placeholder for WebSocket horse pattern - upgrade to DO when needed
    fun connectWebSocket(gameId: String, onUpdate: (String) -> Unit) {
        // ws://worker/ws/gameId
    }
}
