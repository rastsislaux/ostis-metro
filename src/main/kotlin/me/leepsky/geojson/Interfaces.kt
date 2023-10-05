package me.leepsky.geojson

interface WithCoordinates {
    val coordinates: List<Position>
}

interface WithLineStringCoordinates {
    val coordinates: List<List<Position>>
}

interface WithLinearRingCoordinates {
    val coordinates: List<LinearRingCoordinates>
}

interface WithPolygonCoordinates {
    val coordinates: List<List<LinearRingCoordinates>>
}
