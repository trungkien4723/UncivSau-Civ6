# Civ6 Rewrite Spec - UncivSau civ6-rewrite (clean)

Đây là single source of truth cho bản đập đi xây lại, không vá `Civ5` loop. Mọi code mới phải bám spec này.

Nguồn gốc: `CIV6_GAMEPLAY_MECHANICS.md` + `CIV6_PROJECT_COMPARISON.md` (tổng hợp từ Civ VI wiki + CivFanatics).

## 1. Mục tiêu clean
- Xóa `core/src/com/unciv/logic/` và `models/ruleset/` cũ (Civ5: happiness global, 1 tech tree, building queue Civ5, combat Civ5).
- Giữ shell: `android/` (launcher), `desktop/` (PW/desktop), `core/ui/` (LibGDX UI, `UncivGame.kt:470`), `buildSrc/`, `gradle/`, `tests/` framework.
- `CompatibilityVersion.CURRENT_COMPATIBILITY_NUMBER` bump từ `4` -> `10` để cắt save cũ, không cố migrate.

## 2. Data Model v2 (`jsons/Civ6v2/`)

### 2.1 Schema mới (validator `RulesetValidator.kt`)
- `District.json`: `cost`, `adjacencyBonus`, `populationRequirement`, `terrainRequirement`, `specialistSlots`, `maintenance`
- `Building.json`: chỉ nằm trong District (không còn city-center building queue Civ5), `requiredDistrict`, `requiredPopulation`
- `Governments.json`: `policySlots` theo tier + `Government.kt` cards, không còn `PolicyBranch` Civ5
- `Civic.json` tách khỏi `Tech.json`: 2 cây riêng, `eureka`/`inspiration` riêng
- `Governor.json`: `promotions: ArrayList<ArrayList<String>>` (đã fix `Governor.kt:27` CCE), `GovernorManager.kt`

### 2.2 RulesetCache
- `RulesetCache.kt:115` load `BaseRuleset.Civ_VI_v2`, bỏ `Civ_V` fallback.

## 3. Domain modules (`core/src/com/unciv/civ6/`)

```
civ6/
  data/          # JSON DTO + validator v2
  domain/
    city/        # City.kt, CityStats (housing/amenity, không phải happiness global), CityConstructions district queue, CityExpansionManager
    tech/        # TechManager, CivicManager riêng, TechColumn/CivicColumn
    government/  # GovernmentManager, PolicyManager cards
    combat/      # Battle.kt, BattleDamage (support/flanking, walls chỉ +HP không +strength như fix 4.26.5), CityCombatant/MapUnitCombatant
    loyalty/     # LoyaltyManager, EraScore, GoldenAgeManager
    religion/    # ReligionManager
  engine/        # TurnManager, Simulation, GameInfo v2 (version 10)
  ai/            # NextTurnAutomation Civ6
```

## 4. Lộ trình không vá (4 sprint)

- **Sprint1 - City loop:** `City.kt`, `CityStats.kt`, `CityConstructions.kt` rewrite. 1 district / type / city, adjacency tính tại `CityTurnManager`. Test: `CityStatsTest`.
- **Sprint2 - Progression:** Tách `TechManager.kt`/`CivicManager.kt`, `GovernmentManager.kt` cards. Test: civic inspiration trigger.
- **Sprint3 - Combat:** `Battle.kt:1`, `BattleDamage.kt:22`, `BattleConstants.kt` theo Civ6 (walls 0 strength, ranged -17 chỉ cho Siege). Test: `BattleTests`.
- **Sprint4 - Loyalty/Era/Religion/Governor:** `LoyaltyManager`, `Era.kt`, `ReligionManager`, `GovernorManager`. Test: `GovernorPromotionTest`.

## 5. Quy tắc
- Không reuse code `logic/` cũ, viết mới pure Kotlin, coverage >80% trước khi merge.
- Mọi JSON đổi phải có migration hoặc bump `CompatibilityVersion`.
- UI `core/ui/` reuse, chỉ thay presenter `CityScreen.kt`, `TechPickerScreen.kt`, `GovernmentPickerScreen.kt`.

## 6. Branch
- `civ6-rewrite` (orphan) là main mới. `master` giữ `4.26.14` để hotfix, không merge chéo.

## 7. Next step
- Scaffold `core/src/com/unciv/civ6/` + `jsons/Civ6v2/` mẫu + `Versioning.kt` v10, commit `scaffold: civ6 clean base`.
