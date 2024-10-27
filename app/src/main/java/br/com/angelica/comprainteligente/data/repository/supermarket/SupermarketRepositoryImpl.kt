package br.com.angelica.comprainteligente.data.repository.supermarket

import br.com.angelica.comprainteligente.model.Supermarket
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SupermarketRepositoryImpl(
    private val placesClient: PlacesClient,
    private val firestore: FirebaseFirestore
) : SupermarketRepository {

    private val supermarketCollection = firestore.collection("supermarkets")

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

    override suspend fun checkOrCreateSupermarket(supermarketId: String, name: String): Result<Supermarket> {
        return try {
            // Busca o supermercado pelo nome
            val supermarketQuery = supermarketCollection.whereEqualTo("name", name).get().await()
            if (!supermarketQuery.isEmpty) {
                // Retorna o supermercado existente
                val existingSupermarket = supermarketQuery.documents.first().toObject(Supermarket::class.java)
                Result.success(existingSupermarket!!)
            } else {
                // Cria um novo supermercado se ele não existir
                val newSupermarketId = supermarketCollection.document().id
                val newSupermarket = Supermarket(
                    id = newSupermarketId,
                    name = name
                )
                supermarketCollection.document(newSupermarketId).set(newSupermarket).await()
                Result.success(newSupermarket)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

