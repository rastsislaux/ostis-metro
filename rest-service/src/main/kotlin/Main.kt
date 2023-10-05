import com.fasterxml.jackson.databind.node.ArrayNode
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.leepsky.geojson.Feature
import me.leepsky.geojson.FeatureCollection
import me.leepsky.geojson.GeoJSONWriter
import me.leepsky.geojson.Point
import me.leepsky.geojson.Properties
import net.ostis.jesc.api.ScApi
import net.ostis.jesc.client.ScClient
import net.ostis.jesc.client.model.element.ScReference
import net.ostis.jesc.client.model.type.ScType
import net.ostis.jesc.context.ScContext
import net.ostis.jesc.context.ScContextCommon

fun connectScMachine(): Triple<ScClient, ScApi, ScContext> {
    val scClient = ScClient("localhost", 8090)
    val scApi = ScApi(scClient)
    val scContext = ScContextCommon(scApi)

    return Triple<ScClient, ScApi, ScContext>(scClient, scApi, scContext)
}

fun main(args: Array<String>) {

    val (client, api, ctx) = connectScMachine()

    val metroStationClass = ctx.findBySystemIdentifier("metro_station").get()

    val searchResult = api.searchByTemplate().references(
        ScReference.addr(metroStationClass),
        ScReference.type(ScType.EDGE_ACCESS_VAR_POS_PERM),
        ScReference.type(ScType.NODE_VAR)
    ).execute()
    val metroAddrs = searchResult.payload.addrs.map { it.last() }

    val names = metroAddrs.map { ctx.getMainIdentifier(it, "lang_ru") }
    val longitudes =

    /* val names = metroAddrs
        .map { getIdtf(api, nrelMainIdtf, it) }
        .map { (api.content().get(it).execute().payload as ArrayNode)[0]["value"] }

    val longitudes = metroAddrs
        .map { getIdtf(api, nrelLongitude, it) }
        .map { (api.content().get(it).execute().payload as ArrayNode)[0]["value"] }

    val latitudes = metroAddrs
        .map { getIdtf(api, nrelLatitude, it) }
        .map { (api.content().get(it).execute().payload as ArrayNode)[0]["value"] } */

    val geoJSON = FeatureCollection()

    val writer = GeoJSONWriter()
    val str = writer.writeAsString(geoJSON)

    embeddedServer(Netty, 80) { configuration(content = str) }.start(wait = true)
}

private fun Application.configuration(content: String) {
    install(CORS) { anyHost() }
    routing {
        get("/") {
            call.respond(content)
        }
    }
}
