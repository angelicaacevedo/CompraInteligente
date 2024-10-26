package br.com.angelica.comprainteligente.di

import android.util.Log
import br.com.angelica.comprainteligente.data.remote.CorreiosApi
import br.com.angelica.comprainteligente.data.remote.OpenFoodFactsApi
import br.com.angelica.comprainteligente.data.repository.auth.AuthRepository
import br.com.angelica.comprainteligente.data.repository.auth.AuthRepositoryImpl
import br.com.angelica.comprainteligente.data.repository.lists.ProductListRepository
import br.com.angelica.comprainteligente.data.repository.lists.ProductListRepositoryImpl
import br.com.angelica.comprainteligente.data.repository.product.ProductRepository
import br.com.angelica.comprainteligente.data.repository.product.ProductRepositoryImpl
import br.com.angelica.comprainteligente.data.repository.supermarket.SupermarketRepository
import br.com.angelica.comprainteligente.data.repository.supermarket.SupermarketRepositoryImpl
import br.com.angelica.comprainteligente.domain.usecase.CreateListUseCase
import br.com.angelica.comprainteligente.domain.usecase.DeleteListUseCase
import br.com.angelica.comprainteligente.domain.usecase.FetchLatestPricesForListUseCase
import br.com.angelica.comprainteligente.domain.usecase.FetchProductsByListUseCase
import br.com.angelica.comprainteligente.domain.usecase.FetchUserListsUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetCategoriesUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetProductInfoFromBarcodeUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetProductSuggestionsUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetSupermarketSuggestionsUseCase
import br.com.angelica.comprainteligente.domain.usecase.LoginUserUseCase
import br.com.angelica.comprainteligente.domain.usecase.RegisterProductUseCase
import br.com.angelica.comprainteligente.domain.usecase.RegisterUserUseCase
import br.com.angelica.comprainteligente.domain.usecase.UpdateListUseCase
import br.com.angelica.comprainteligente.presentation.viewmodel.AuthViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductListViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductViewModel
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    // Retrofit instance for Correios API
    single(named("CorreiosRetrofit")) {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Correios API instance
    single<CorreiosApi> { get<Retrofit>(named("CorreiosRetrofit")).create(CorreiosApi::class.java) }

    // PlacesClient for Google Places API
    single {
        Places.createClient(androidContext())  // Inicializa o PlacesClient
    }

    // Retrofit para OpenFoodFacts
    single(named("OpenFoodFactsRetrofit")) {
        Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // OpenFoodFacts API
    single<OpenFoodFactsApi> { get<Retrofit>(named("OpenFoodFactsRetrofit")).create(OpenFoodFactsApi::class.java) }

    // Firebase dependencies
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }

    // Repositories
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<ProductRepository> { ProductRepositoryImpl(get(), get()) }
    single<SupermarketRepository> { SupermarketRepositoryImpl(get()) }
    single<ProductListRepository> { ProductListRepositoryImpl(get()) }

    // Use Cases
    factory { RegisterUserUseCase(get()) }
    factory { LoginUserUseCase(get()) }
    factory { GetProductInfoFromBarcodeUseCase(get()) }
    factory { GetSupermarketSuggestionsUseCase(get()) }
    factory { RegisterProductUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { FetchUserListsUseCase(get()) }
    factory { CreateListUseCase(get()) }
    factory { DeleteListUseCase(get()) }
    factory { GetProductSuggestionsUseCase(get()) }
    factory { FetchProductsByListUseCase(get()) }
    factory { UpdateListUseCase(get()) }
    factory { FetchLatestPricesForListUseCase(get()) }

    // ViewModels
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { ProductViewModel(get(), get(), get()) }
    viewModel { ProductListViewModel(get(), get(), get(), get(), get(), get(), get()) }
}
