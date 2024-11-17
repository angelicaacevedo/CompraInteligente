package br.com.angelica.comprainteligente.data.repository.supermarket

import br.com.angelica.comprainteligente.model.Supermarket
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SupermarketRepositoryImpl(
    private val placesClient: PlacesClient,
    private val firestore: FirebaseFirestore
) : SupermarketRepository {

    private val supermarketCollection = firestore.collection("supermarkets")

    override suspend fun getSupermarketSuggestions(query: String): List<Pair<String, String>> {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        return try {
            val response = placesClient.findAutocompletePredictions(request).await()
            response.autocompletePredictions.map { prediction ->
                prediction.getFullText(null).toString() to prediction.placeId
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun checkOrCreateSupermarket(
        placeId: String,
        name: String
    ): Result<Supermarket> {
        return try {
            // Verifica se o supermercado com o `placeId` já existe
            val existingSupermarketQuery = supermarketCollection.whereEqualTo("id", placeId).get().await()
            if (existingSupermarketQuery.documents.isNotEmpty()) {
                // Retorna o supermercado existente
                val existingSupermarket = existingSupermarketQuery.documents.first().toObject(Supermarket::class.java)
                return Result.success(existingSupermarket!!)
            }

            // Se o supermercado não existir, faz a solicitação de detalhes e cria um novo
            val placeDetailsRequest = FetchPlaceRequest.newInstance(
                placeId,
                listOf(Place.Field.ADDRESS_COMPONENTS, Place.Field.FORMATTED_ADDRESS)
            )
            val placeDetailsResponse = placesClient.fetchPlace(placeDetailsRequest).await()
            val place = placeDetailsResponse.place

            // Extração de cada componente do endereço
            val street = place.addressComponents?.asList()?.find { it.types.contains("route") }?.name ?: ""
            val city = place.addressComponents?.asList()?.find {
                it.types.contains("locality") || it.types.contains("administrative_area_level_2")
            }?.name ?: ""
            val state = place.addressComponents?.asList()?.find { it.types.contains("administrative_area_level_1") }?.name ?: ""
            val zipCode = place.addressComponents?.asList()?.find { it.types.contains("postal_code") }?.name ?: ""

            // Cria um novo supermercado com os dados completos do endereço
            val newSupermarket = Supermarket(
                id = placeId,
                name = name,
                street = street,
                city = city,
                state = state,
                zipCode = zipCode
            )
            supermarketCollection.document(placeId).set(newSupermarket).await()
            Result.success(newSupermarket)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllSupermarkets(): List<Supermarket> {
        return try {
            val snapshot = supermarketCollection.get().await()
            snapshot.toObjects(Supermarket::class.java)
        } catch (e: Exception) {
            emptyList() // Retorna uma lista vazia em caso de erro
        }
    }
}

