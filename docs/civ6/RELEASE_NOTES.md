# Release Notes - civ6-rewrite v1.0 (clean)

Branch `civ6-rewrite` (orphan, 21 commits) is the first clean Civ6 implementation, no Civ5 loop.

## What's new vs master 4.26.14
- **Engine v10** (`Civ6CompatibilityVersion 10`) cuts Civ5 saves, fixes misleading "incompatible version" error from `UncivFiles.kt:415`
- **City v2**: Housing/Amenity (not global happiness), `maxDistrictSlots 1,4,7...`, adjacency `+0.5` chaining, district cost `+15` scaling
- **Tech/Civic split**: 2 trees, eureka/inspiration `+50%`, Government slots `Military/Economic/Diplomatic/Wildcard`
- **Combat v2**: walls HP only (not `200/400` strength), siege `-17` vs land only, support/flanking `+2`
- **Loyalty/Era/Governor/Religion**: pressure, Heroic Age, `ArrayList` fix CCE, pantheon 25/prophet 60
- **Map/Save v10**: `TileMapV2` district loses resource, `Civ6Files` only `>10` incompatible
- **AI**: `City/Tech/Government` automation
- **Multiplayer**: Cloudflare Worker `flagfish` (KV/D1+R2) + `horse` Durable Object WS realtime + push notifications + workshop R2
- **Balancing**: central `Balancing.kt` + `Performance` LRU caches

## Migration
- Old saves `v4` (Civ5) not compatible - start new game on `civ6-rewrite`.
- To merge to `master`: squash or keep `civ6-rewrite` as new main, archive `master` as `legacy-civ5`.

## Testing
- `tests/src/com/unciv/civ6/` 12 suites, `FullGameTest` 10-turn integration.
- Manual: `Civ6GameStarter.newGame` -> `Civ6WorldScreen` -> `TurnManagerWithAI`.

## Next
- UI polish: `WorldScreen` navigation from `Civ6WorldScreen`, `CityScreen` district placement lens.
- Balancing QA with community.

Generated for Sprint20.
