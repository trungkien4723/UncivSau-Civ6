# Architecture - civ6-rewrite

Clean Civ6, no Civ5 loop. 8 sprints on `civ6-rewrite` branch (orphan, 844e7c0 base).

## Module layout `core/src/com/unciv/civ6/`

```
civ6/
  data/         DistrictData.kt, BuildingData (v2 JSON)
  domain/city/  City.kt, CityStats.kt (Housing/Amenity), Adjacency.kt, CityConstructions.kt
  domain/tech/  TechCivic.kt (TechManager/CivicManager, eureka 50%)
  domain/government/ Government.kt (slots Military/Economic/Diplomatic/Wildcard) + Governor.kt (ArrayList fix)
  domain/combat/ Combatant.kt, BattleDamage.kt (30*exp), Battle.kt (walls HP only)
  domain/loyalty/ Loyalty.kt (pressure, EraScore)
  domain/religion/ Religion.kt (pantheon 25, prophet 60, evangelize)
  map/          TileMapV2.kt (district loses resource, harbor coast, radius 37)
  engine/       Versioning.kt v10, GameInfoV2.kt, TurnManager.kt, Civ6Files.kt (only >10 incompatible), TurnManagerWithAI.kt
  ai/           CityAutomation.kt, TechAutomation.kt, GovernmentAutomation.kt
  ui/           Civ6WorldScreen.kt, Civ6CityScreen.kt, Civ6GameStarter.kt
```

## Data `jsons/Civ6v2/`
Districts, Buildings, Techs, Civics, Governments, PolicyCards - v2 schema, validator future.

## Engine
- `Civ6CompatibilityVersion.CURRENT_NUMBER=10` cuts Civ5 saves (master v4)
- `GameInfoV2` + `TurnManager` ticks city/loyalty/governor per turn
- `Civ6Files` save/load JSON plain, only >10 throws incompatible (fixed misleading error from master UncivFiles.kt:415)

## Tests `tests/src/com/unciv/civ6/`
CityStats, Adjacency, City, TechCivic, Government, Combat, Loyalty, Religion, TileMap, Civ6Files, AI, UI - coverage >80% per sprint.

## Roadmap done
Sprint1 City 7cf7816, Sprint2 Tech/Gov 439dacf, Sprint3 Combat 4bb6949, Sprint4 Loyalty  b7f5ad2, Sprint5 Engine 867577e, Sprint6 UI 3c76a55, Sprint7 Map/Save d8a6790, Sprint8 AI 7e5ab5e, Sprint9 Polish (this).
Master stays 4.26.14 for hotfix, no merge back.
