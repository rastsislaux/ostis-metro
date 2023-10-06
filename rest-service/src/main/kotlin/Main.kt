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

fun connectScMachine(): ScContext {
    val scClient = ScClient("localhost", 8090)
    val scApi = ScApi(scClient)
    val scContext = ScContextCommon(scApi)

    return scContext
}

fun getRelationTargets(ctx: ScContext, addr: Long, relAddr: Long, relType: ScType) =
    ctx.api.searchByTemplate()
        .references(
            ScReference.addr(addr),
            ScReference.type(relType, "edge"),
            ScReference.type(ScType.VAR)
        )
        .references(
            ScReference.addr(relAddr),
            ScReference.type(ScType.EDGE_ACCESS_VAR_POS_PERM),
            ScReference.alias("edge")
        )
        .execute()
        .payload
        .addrs
        .map { it[2] }

fun getRelationTarget(ctx: ScContext, addr: Long, relAddr: Long, relType: ScType): Long =
    getRelationTargets(ctx, addr, relAddr, relType)[0]

fun getRoleRelationTargets(ctx: ScContext, addr: Long, rrelAddr: Long) =
    getRelationTargets(ctx, addr, rrelAddr, ScType.EDGE_ACCESS_VAR_POS_PERM)

fun getRoleRelationTarget(ctx: ScContext, addr: Long, rrelAddr: Long) =
    getRelationTarget(ctx, addr, rrelAddr, ScType.EDGE_ACCESS_VAR_POS_PERM)

fun getNoRoleRelationTarget(ctx: ScContext, addr: Long, nrelAddr: Long) =
    getRelationTarget(ctx, addr, nrelAddr, ScType.EDGE_D_COMMON_VAR)

fun getElements(ctx: ScContext, setAddr: Long) = ctx.api.searchByTemplate().references(
    ScReference.addr(setAddr),
    ScReference.type(ScType.EDGE_ACCESS_VAR_POS_PERM),
    ScReference.type(ScType.NODE_VAR)
).execute().payload.addrs.map { it.last() }

fun getGeoposition(ctx: ScContext, addr: Long): Pair<Double, Double> {
    val nrelGeoposition = ctx.findBySystemIdentifier("nrel_geoposition").get()
    val rrelLongitude   = ctx.findBySystemIdentifier("rrel_longitude").get()
    val rrelLatitude    = ctx.findBySystemIdentifier("rrel_latitude").get()

    val geopositionAddr = getNoRoleRelationTarget(ctx, addr, nrelGeoposition)

    val longitudeAddr = getRoleRelationTarget(ctx, geopositionAddr, rrelLongitude)
    val latitudeAddr  = getRoleRelationTarget(ctx, geopositionAddr, rrelLatitude)

    val longitude = ctx.getLinkContent(longitudeAddr).asDouble()
    val latitude  = ctx.getLinkContent(latitudeAddr).asDouble()

    return Pair(longitude, latitude)
}

const val BLUE_LINE_IDTF  = "metro_line_Maskouskaja"
const val RED_LINE_IDTF   = "metro_line_Autazavodskaja"
const val GREEN_LINE_IDTF = "metro_line_Zelenaluzhskaja"

fun createFeatureCollectionForMetroLine(ctx: ScContext, metroLineAddr: Long): FeatureCollection {
    val rrelStation = ctx.findBySystemIdentifier("rrel_station").get()

    return getRoleRelationTargets(ctx, metroLineAddr, rrelStation)
        .map {
            val geoposition = getGeoposition(ctx, it)
            return@map Feature(
                geometry = Point(geoposition.first, geoposition.second),
                properties = Properties { name = ctx.getMainIdentifier(it, "lang_ru") }
            )
        }
        .let { FeatureCollection(it) }
}

fun main(args: Array<String>) {
    val ctx = connectScMachine()

    val metroLineClass = ctx.findBySystemIdentifier("metro_line").get()

    val metroLines = getElements(ctx, metroLineClass)
        .associateWith { ctx.getSystemIdentifier(it) }
        .entries.associate { (key, value) -> value to key }

    val blueStations  = createFeatureCollectionForMetroLine(ctx, metroLines[BLUE_LINE_IDTF]!!)
    val redStations   = createFeatureCollectionForMetroLine(ctx, metroLines[RED_LINE_IDTF]!!)
    val greenStations = createFeatureCollectionForMetroLine(ctx, metroLines[GREEN_LINE_IDTF]!!)

    embeddedServer(Netty, 8002) {
        configuration(
            blueStations  = blueStations,
            redStations   = redStations,
            greenStations = greenStations
        )
    }.start()
}

private fun Application.configuration(
    blueStations: FeatureCollection,
    redStations: FeatureCollection,
    greenStations: FeatureCollection
) {
    install(CORS) { anyHost() }

    val writer = GeoJSONWriter()
    val blueStationsJSON  = writer.writeAsString(blueStations)
    val redStationsJSON   = writer.writeAsString(redStations)
    val greenStationsJSON = writer.writeAsString(greenStations)

    routing {
        get("/stations/blue")  { call.respond(blueStationsJSON)  }
        get("/stations/red")   { call.respond(redStationsJSON)   }
        get("/stations/green") { call.respond(greenStationsJSON) }
    }
}
