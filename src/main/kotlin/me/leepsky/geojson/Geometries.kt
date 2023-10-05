package me.leepsky.geojson

abstract class Geometry(type: ObjectType): GeoJSONObject(type)

class Position(coordinates: List<Double>): ArrayList<Double>(coordinates) {

    constructor(longitude: Double, latitude: Double): this(arrayListOf()) {
        this.add(longitude)
        this.add(latitude)
    }

}

class Point(val coordinates: Position) : Geometry(ObjectType.POINT) {

    constructor(longitude: Double, latitude: Double, altitude: Double? = null) :
            this(
                Position(mutableListOf(longitude, latitude))
            ) {
        altitude?.let { this.coordinates.add(it) }
    }

}

class MultiPoint(override val coordinates: List<Position>): Geometry(ObjectType.MULTI_POINT), WithCoordinates

class LineString(override val coordinates: List<Position>): Geometry(ObjectType.LINE_STRING), WithCoordinates {
    constructor(vararg coordinates: Position) : this(coordinates.asList())
}

class MultiLineString(override val coordinates: List<List<Position>>): Geometry(ObjectType.MULTI_LINE_STRING), WithLineStringCoordinates

class Polygon(override val coordinates: List<LinearRingCoordinates>): Geometry(ObjectType.POLYGON), WithLinearRingCoordinates {

    init {
        if (coordinates.isEmpty()) {
            throw InvalidCoordinatesException("For Polygon there must be at least one coordinate.")
        }
    }

}

class MultiPolygon(override val coordinates: List<List<LinearRingCoordinates>>): Geometry(ObjectType.MULTI_POLYGON), WithPolygonCoordinates

class GeometryCollection(val geometries: List<Geometry>): Geometry(ObjectType.GEOMETRY_COLLECTION)
