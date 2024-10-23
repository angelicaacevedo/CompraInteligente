package br.com.angelica.comprainteligente.data.remote

import br.com.angelica.comprainteligente.model.remote.AddressResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CorreiosApi {
    @GET("{cep}/json/")
    suspend fun getAddressByCep(@Path("cep") cep: String): Response<AddressResponse>
}