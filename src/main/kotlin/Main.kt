import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.leepsky.geojson.Feature
import me.leepsky.geojson.FeatureCollection
import me.leepsky.geojson.GeoJSONWriter
import me.leepsky.geojson.LineString
import me.leepsky.geojson.Point
import me.leepsky.geojson.Position
import me.leepsky.geojson.Properties

fun makeSomeGeoJSON(): String {
    val collection = FeatureCollection(
        Feature(
            geometry = Point(longitude = 27.59498, latitude = 53.91171),
            properties = Properties {
                name = "BSUIR"
                description = "Some description for point 1."
            }
        ),
        Feature(
            geometry = Point(longitude = 29.3257, latitude = 52.1323),
            properties = Properties {
                name = "Kalinkovichi"
                description = "Some description for point 2."
            }
        ),
        Feature(
            geometry = LineString(
                Position(27.59498, 53.91171),
                Position(29.3257, 52.1323)
            ),
            properties = Properties {
                name = "Line 1"
                description = "Some description for line 1."
            }
        )
    )
    val writer = GeoJSONWriter()
    return writer.writeAsString(collection)
}

fun main(args: Array<String>) {
    val geojson = makeSomeGeoJSON()

    embeddedServer(Netty, 8080) {
        configuration(content = geojson)
    }.start(wait = true)

}

private fun Application.configuration(content: String) {
    install(CORS) {
        anyHost()
    }
    routing {
        get("/") {
            call.respond(content)
        }
    }
}