/**
 * Cloudflare Worker for Civ6 multiplayer - flagfish/horse pattern.
 * KV/D1 + R2 backup. Stores GameInfoV2 JSON v10.
 * Deploy: npx wrangler deploy
 */

export interface Env {
  GAMES: KVNamespace // or DB: D1Database
  BACKUPS: R2Bucket
  AUTH_TOKEN?: string
}

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const url = new URL(request.url)
    const auth = request.headers.get("Authorization")
    if (env.AUTH_TOKEN && auth !== `Bearer ${env.AUTH_TOKEN}`) {
      // Allow read without auth, write requires token - adjust as needed
      if (request.method !== "GET") return new Response("Unauthorized", { status: 401 })
    }

    // GET /game/:id
    const gameMatch = url.pathname.match(/^\/game\/([^/]+)$/)
    if (gameMatch) {
      const gameId = gameMatch[1]
      if (request.method === "GET") {
        const json = await env.GAMES.get(`game:${gameId}`)
        if (!json) return new Response("Not found", { status: 404 })
        return new Response(json, { headers: { "Content-Type": "application/json" } })
      }
      if (request.method === "PUT") {
        const body = await request.text()
        // Check If-Match for conflict (checksum)
        const ifMatch = request.headers.get("If-Match")
        const existing = await env.GAMES.get(`game:${gameId}`)
        if (ifMatch && existing) {
          const existingPreview = JSON.parse(existing).checksum || ""
          if (ifMatch !== existingPreview) return new Response("Conflict - reload", { status: 409 })
        }
        await env.GAMES.put(`game:${gameId}`, body)
        // Backup to R2 hourly via scheduled event in production
        return new Response("OK")
      }
      if (request.method === "DELETE") {
        await env.GAMES.delete(`game:${gameId}`)
        return new Response("OK")
      }
    }

    // GET /games?playerId=xxx
    if (url.pathname === "/games" && request.method === "GET") {
      const playerId = url.searchParams.get("playerId")
      // KV list - in production use D1 query: SELECT gameId FROM games WHERE playerId = ?
      const list = await env.GAMES.list({ prefix: "game:" })
      const games = list.keys.map(k => k.name.replace("game:", ""))
      // Filter by playerId if needed via preview JSON
      return new Response(JSON.stringify(games), { headers: { "Content-Type": "application/json" } })
    }

    // Horse-like WebSocket for realtime (Durable Object) - placeholder
    // if (url.pathname.startsWith("/ws/")) return handleWebSocket(request, env)

    return new Response("Unciv Civ6 Worker - flagfish/horse pattern. Use /game/:id and /games?playerId=xxx", { status: 200 })
  },

  // Scheduled backup to R2 (cron)
  async scheduled(event: ScheduledEvent, env: Env) {
    // Copy KV to R2 daily
  }
}
