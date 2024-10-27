package br.com.angelica.comprainteligente.model.remote

data class PlacesResponse(
    val results: List<PlaceResult>
)

data class PlaceResult(
    val placeId: String,
    val name: String,
    val formattedAddress: String
)

