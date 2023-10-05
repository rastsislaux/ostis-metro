package me.leepsky.geojson

class Feature(
    val geometry: Geometry?,
    val properties: Map<String, String>
): GeoJSONObject(ObjectType.FEATURE)


data class FeatureCollection(val features: List<Feature>): GeoJSONObject(ObjectType.FEATURE_COLLECTION) {

    constructor(vararg features: Feature) : this(features = features.asList())

}
