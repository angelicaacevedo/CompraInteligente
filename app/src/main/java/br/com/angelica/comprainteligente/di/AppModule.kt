package br.com.angelica.comprainteligente.di

import br.com.angelica.comprainteligente.data.remote.CorreiosApi
import br.com.angelica.comprainteligente.data.remote.OpenFoodFactsApi
import br.com.angelica.comprainteligente.data.repository.auth.AuthRepository
import br.com.angelica.comprainteligente.data.repository.auth.AuthRepositoryImpl
import br.com.angelica.comprainteligente.data.repository.product.ProductRepository
import br.com.angelica.comprainteligente.data.repository.product.ProductRepositoryImpl
import br.com.angelica.comprainteligente.data.repository.supermarket.SupermarketRepository
import br.com.angelica.comprainteligente.data.repository.supermarket.SupermarketRepositoryImpl
import br.com.angelica.comprainteligente.domain.usecase.GetProductInfoFromBarcodeUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetSupermarketSuggestionsUseCase
import br.com.angelica.comprainteligente.domain.usecase.LoginUserUseCase
import br.com.angelica.comprainteligente.domain.usecase.RegisterProductUseCase
import br.com.angelica.comprainteligente.domain.usecase.RegisterUserUseCase
import br.com.angelica.comprainteligente.presentation.viewmodel.AuthViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductViewModel
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    // Retrofit instance for Correios API
    single {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Correios API instance
    single<CorreiosApi> { get<Retrofit>().create(CorreiosApi::class.java) }

    // PlacesClient for Google Places API
    single {
        Places.createClient(androidContext())  // Inicializa o PlacesClient
    }

    // Retrofit para OpenFoodFacts
    single {
        Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // OpenFoodFacts API
    single<OpenFoodFactsApi> { get<Retrofit>().create(OpenFoodFactsApi::class.java) }


    // Firebase dependencies
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<ProductRepository> { ProductRepositoryImpl(get(), get()) }
    single<SupermarketRepository> { SupermarketRepositoryImpl(get()) }

    // Use Cases
    factory { RegisterUserUseCase(get()) }
    factory { LoginUserUseCase(get()) }
    factory { GetProductInfoFromBarcodeUseCase(get()) }
    factory { GetSupermarketSuggestionsUseCase(get()) }
    factory { RegisterProductUseCase(get()) }

    // ViewModels
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { ProductViewModel(get(), get(), get()) }
}
