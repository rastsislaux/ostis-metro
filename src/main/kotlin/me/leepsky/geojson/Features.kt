package me.leepsky.geojson

import java.io.Serializable

class Feature(
    val geometry: Geometry?,
    val properties: Map<String, String>
): GeoJSONObject(ObjectType.FEATURE)


data class FeatureCollection(val features: List<Feature>): GeoJSONObject(ObjectType.FEATURE_COLLECTION) {

    constructor(vararg features: Feature) : this(features = features.asList())

}
