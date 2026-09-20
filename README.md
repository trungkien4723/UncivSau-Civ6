# UncivSau-Civ6 - Clean Civ6 from scratch

No Civ5 base. LibGDX + Kotlin pure, ported from `civ6-rewrite` domain logic.

- Engine v10 (`GameInfoV2` `Civ6Compatibility 10`)
- City v2 Housing/Amenity + adjacency
- Tech/Civic split + Government cards
- Combat walls HP only
- Full 33 sprints from `UncivSau` `civ6-rewrite` branch

Ported from `trungkien4723/UncivSau` `civ6-rewrite` `844e7c0`..`1991aa2`.

## Structure
- `core/src/com/unciv/civ6/` - all domain logic (city, tech, combat, etc.)
- `jsons/Civ6v2/` - v2 JSONs
- `worker/` - Cloudflare Worker for MP

No `core/logic` Civ5.

See `docs/civ6-spec.md` for single truth.
