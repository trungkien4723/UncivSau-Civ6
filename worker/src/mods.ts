/**
 * Workshop for Civ6 mods - R2 bucket.
 * PUT /mods/:name.zip (upload), GET /mods/:name.zip (download), GET /mods (list)
 */

export async function handleMods(request: Request, env: any): Promise<Response> {
  const url = new URL(request.url)
  const modMatch = url.pathname.match(/^\/mods\/([^/]+)\.zip$/)
  if (modMatch) {
    const modName = modMatch[1]
    if (request.method === "PUT") {
      const data = await request.arrayBuffer()
      await env.BACKUPS.put(`mods/${modName}.zip`, data)
      return new Response("Uploaded")
    }
    if (request.method === "GET") {
      const obj = await env.BACKUPS.get(`mods/${modName}.zip`)
      if (!obj) return new Response("Not found", { status: 404 })
      return new Response(obj.body, { headers: { "Content-Type": "application/zip" } })
    }
  }
  if (url.pathname === "/mods" && request.method === "GET") {
    const list = await env.BACKUPS.list({ prefix: "mods/" })
    const names = list.objects.map((o: any) => o.key.replace("mods/", "").replace(".zip", ""))
    return new Response(JSON.stringify(names), { headers: { "Content-Type": "application/json" } })
  }
  return new Response("Not found", { status: 404 })
}
