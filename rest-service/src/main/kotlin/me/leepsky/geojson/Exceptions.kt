package me.leepsky.geojson

open class GeoJSONException(msg: String): RuntimeException(msg)

class InvalidCoordinatesException(msg: String): GeoJSONException(msg)
