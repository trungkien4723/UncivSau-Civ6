# Civ6 Clean Module

Package `com.unciv.civ6` là rewrite sạch, không kế thừa `com.unciv.logic` Civ5.

- `data/` - DTO + validator cho `jsons/Civ6v2/`
- `domain/city/` - housing/amenity, district queue
- `domain/tech/` - tech & civic riêng
- `domain/government/` - policy cards
- `domain/combat/` - battle Civ6
- `domain/loyalty/` - loyalty/era
- `engine/` - GameInfo v10, TurnManager
- `ai/` - automation

Mọi file mới phải pure Kotlin, có test trong `tests/src/com/unciv/civ6/`.
