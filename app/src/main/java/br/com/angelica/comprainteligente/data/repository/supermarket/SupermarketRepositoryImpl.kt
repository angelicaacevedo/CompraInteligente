package br.com.angelica.comprainteligente.data.repository.supermarket

import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.tasks.await


class SupermarketRepositoryImpl(private val placesClient: PlacesClient) : SupermarketRepository {
    override suspend fun getSupermarketSuggestions(query: String): List<String> {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        return try {
            val response = placesClient.findAutocompletePredictions(request).await()
            response.autocompletePredictions.map { prediction ->
                prediction.getFullText(null).toString()  // Usando null para CharacterStyle
            }
        } catch (e: Exception) {
            emptyList()  // Se ocorrer algum erro, retorna uma lista vazia
        }
    }
}
