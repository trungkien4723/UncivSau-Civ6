package com.unciv.civ6.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.unciv.civ6.domain.city.AdjacencyContext
import com.unciv.civ6.domain.city.City
import com.unciv.ui.screens.basescreen.BaseScreen
import com.unciv.ui.components.extensions.toLabel
import com.unciv.ui.components.extensions.toTextButton

/**
 * Full CityScreen for Civ6 - district placement with adjacency preview.
 */
class Civ6CityScreenFull(private val city: City) : BaseScreen() {
    init {
        val table = Table()
        table.add("${city.name} - Pop ${city.population}".toLabel()).row()
        table.add("Housing ${city.housing()} Amenities ${city.amenities()}".toLabel()).row()
        table.add("Districts ${city.districts.size}/${city.maxDistrictSlots()}".toLabel()).row()
        for (d in city.districts) {
            table.add("${d.district.name} at ${d.location} adj ${d.adjacencyYield}".toLabel()).row()
        }
        // Adjacency preview button
        table.add("Place District (preview)".toTextButton().apply {
            // In real UI, click opens lens to choose location and shows adjacency
        }).row()
        stage.addActor(table)
        table.setFillParent(true)
    }

    fun previewAdjacency(districtName: String, context: AdjacencyContext): String {
        val d = city.districts.find { it.district.name == districtName }?.district ?: return "Not found"
        return "Adjacency: ${city.adjacencyForNewDistrict(d, context)}"
    }
}
