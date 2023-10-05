package me.leepsky.geojson

class Properties(init: Properties.() -> Unit): HashMap<String, String>() {

    init {
        init()
    }

    var name: String?
        set(value) {
            value?.let { this["name"] = value }
        }
        get() = this["name"]

    var description: String?
        set(value) {
            value?.let { this["description"] = value }
        }
        get() = this["description"]

}