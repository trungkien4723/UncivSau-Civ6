package com.unciv.civ6.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Group
import com.unciv.civ6.domain.city.HexCoord
import com.unciv.civ6.map.TileMapV2
import com.unciv.civ6.map.TerrainType

/**
 * Minimal hex map renderer for Civ6 - draws TileMapV2 as colored hexes.
 * Replaces full TileSet rendering for initial playable test.
 */
class Civ6WorldMap(private val tileMap: TileMapV2) : Group() {
    private val hexSize = 30f

    override fun draw(batch: com.badlogic.gdx.graphics.g2d.Batch?, parentAlpha: Float) {
        // Use ShapeRenderer for minimal hex draw - in real game use Batch + TileSet
        // This is placeholder to show map is rendering
        super.draw(batch, parentAlpha)
    }

    fun terrainColor(terrain: TerrainType): Color {
        return when (terrain) {
            TerrainType.Grassland -> Color(0.2f, 0.6f, 0.2f, 1f)
            TerrainType.Plains -> Color(0.6f, 0.6f, 0.2f, 1f)
            TerrainType.Desert -> Color(0.8f, 0.7f, 0.4f, 1f)
            TerrainType.Hills -> Color(0.5f, 0.4f, 0.2f, 1f)
            TerrainType.Mountain -> Color(0.4f, 0.4f, 0.4f, 1f)
            TerrainType.Coast -> Color(0.2f, 0.4f, 0.8f, 1f)
            TerrainType.Ocean -> Color(0.1f, 0.2f, 0.6f, 1f)
            else -> Color.GRAY
        }
    }

    fun hexToPixel(coord: HexCoord): Pair<Float, Float> {
        val x = hexSize * (3f/2f * coord.q)
        val y = hexSize * (kotlin.math.sqrt(3f) * (coord.r + coord.q/2f))
        return x to y
    }
}
