/**
 * Horse Durable Object for Civ6 realtime - WebSocket per gameId.
 * Each gameId maps to one DO instance, all players WS connect to /ws/:gameId .
 */

export class GameRoom implements DurableObject {
  private sessions: Map<WebSocket, string> = new Map() // ws -> playerId

  constructor(private state: DurableObjectState, private env: any) {}

  async fetch(request: Request): Promise<Response> {
    const url = new URL(request.url)
    if (url.pathname.startsWith("/ws/")) {
      if (request.headers.get("Upgrade") !== "websocket") return new Response("Need WS", { status: 426 })
      const pair = new WebSocketPair()
      const [client, server] = Object.values(pair)
      this.handleSession(server, url.searchParams.get("playerId") || "anon")
      return new Response(null, { status: 101, webSocket: client })
    }
    // REST fallback for game state in DO storage
    const gameId = url.pathname.split("/")[2]
    if (request.method === "GET") {
      const data = await this.state.storage.get(`game:${gameId}`)
      return new Response(JSON.stringify(data || {}), { headers: { "Content-Type": "application/json" } })
    }
    if (request.method === "PUT") {
      const body = await request.text()
      await this.state.storage.put(`game:${gameId}`, body)
      this.broadcast(JSON.stringify({ type: "update", gameId }))
      return new Response("OK")
    }
    return new Response("Not found", { status: 404 })
  }

  handleSession(ws: WebSocket, playerId: string) {
    ws.accept()
    this.sessions.set(ws, playerId)
    ws.addEventListener("message", async (msg) => {
      // Relay to all other sessions in room
      this.broadcast(msg.data as string, ws)
    })
    ws.addEventListener("close", () => this.sessions.delete(ws))
    // Send initial state
    ws.send(JSON.stringify({ type: "connected", playerId }))
  }

  broadcast(message: string, exclude?: WebSocket) {
    for (const [ws] of this.sessions) {
      if (ws !== exclude) try { ws.send(message) } catch {}
    }
  }
}
