package br.com.angelica.comprainteligente.data.remote

import br.com.angelica.comprainteligente.model.remote.OpenFoodFactsProductResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsApi {
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): OpenFoodFactsProductResponse
}
