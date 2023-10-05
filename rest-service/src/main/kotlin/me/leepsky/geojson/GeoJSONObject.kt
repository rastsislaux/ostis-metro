package me.leepsky.geojson

import com.fasterxml.jackson.annotation.JsonValue

enum class ObjectType(
    @JsonValue
    val jsonValue: String
) {
    POINT("Point"),
    MULTI_POINT("MultiPoint"),
    LINE_STRING("LineString"),
    MULTI_LINE_STRING("MultiLineString"),
    POLYGON("Polygon"),
    MULTI_POLYGON("MultiPolygon"),
    GEOMETRY_COLLECTION("GeometryCollection"),
    FEATURE("Feature"),
    FEATURE_COLLECTION("FeatureCollection");
}

open class GeoJSONObject(
    val type: ObjectType
)



class LinearRingCoordinates(private val coordinates: List<Position>): ArrayList<Position>(coordinates) {

    constructor(vararg coordinates: Position): this(coordinates.asList())

    init {
        when {
            coordinates.size < 4 ->
                throw InvalidCoordinatesException("For LinearRing there must be at least 4 positions.")
            coordinates[0] != coordinates[coordinates.size - 1] ->
                throw InvalidCoordinatesException("For LinearRing first and last position must be the same.")
        }
    }

}
