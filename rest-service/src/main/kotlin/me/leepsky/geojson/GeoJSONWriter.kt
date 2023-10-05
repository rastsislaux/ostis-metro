package me.leepsky.geojson

import com.fasterxml.jackson.databind.ObjectMapper

class GeoJSONWriter(private val objectMapper: ObjectMapper) {

    constructor(): this(ObjectMapper())

    fun writeAsString(obj: GeoJSONObject): String {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj)
    }

}
