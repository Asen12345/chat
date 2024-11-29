import kotlinx.serialization.Serializable


@Serializable
    data class MapMarker_DATA(
        var id: Int = 0,
        val name: String,
        val type: String,
        val lon: Double,
        val lat: Double,
        val description: String,
        val visitTime: String,
        val isVisited: Boolean,
        val imageUrl: String,
        val queryPrompt: String
    )
