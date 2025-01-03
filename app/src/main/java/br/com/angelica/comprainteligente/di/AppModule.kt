package br.com.angelica.comprainteligente.di

import br.com.angelica.comprainteligente.data.SessionManager
import br.com.angelica.comprainteligente.data.remote.CorreiosApi
import br.com.angelica.comprainteligente.data.remote.OpenFoodFactsApi
import br.com.angelica.comprainteligente.data.repository.auth.AuthRepository
import br.com.angelica.comprainteligente.data.repository.auth.AuthRepositoryImpl
import br.com.angelica.comprainteligente.data.repository.lists.ProductListRepository
import br.com.angelica.comprainteligente.data.repository.lists.ProductListRepositoryImpl
import br.com.angelica.comprainteligente.data.repository.price.PriceRepository
import br.com.angelica.comprainteligente.data.repository.price.PriceRepositoryImpl
import br.com.angelica.comprainteligente.data.repository.product.ProductRepository
import br.com.angelica.comprainteligente.data.repository.product.ProductRepositoryImpl
import br.com.angelica.comprainteligente.data.repository.supermarket.SupermarketRepository
import br.com.angelica.comprainteligente.data.repository.supermarket.SupermarketRepositoryImpl
import br.com.angelica.comprainteligente.domain.usecase.AuthUseCases
import br.com.angelica.comprainteligente.domain.usecase.GetCategoriesUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetPriceHistoryUseCase
import br.com.angelica.comprainteligente.domain.usecase.GetSupermarketSuggestionsUseCase
import br.com.angelica.comprainteligente.domain.usecase.MonthlySummaryUseCase
import br.com.angelica.comprainteligente.domain.usecase.ProductListOperationsUseCase
import br.com.angelica.comprainteligente.domain.usecase.ProductOperationsUseCase
import br.com.angelica.comprainteligente.domain.usecase.RecentPurchasesUseCase
import br.com.angelica.comprainteligente.domain.usecase.UserProgressUseCase
import br.com.angelica.comprainteligente.model.CategoryRepository
import br.com.angelica.comprainteligente.presentation.viewmodel.AuthViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.HomeViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.InflationViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductListViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.UserProfileViewModel
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

    // Register SessionManager as a singleton
    single { SessionManager(androidContext()) }

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
    single<SupermarketRepository> { SupermarketRepositoryImpl(get(),get()) }
    single<PriceRepository> { PriceRepositoryImpl(get()) }
    single<ProductListRepository> { ProductListRepositoryImpl(get()) }

    // CategoryRepository (como singleton)
    single { CategoryRepository }

    // Use Cases
    factory { AuthUseCases(get()) }
    factory { GetSupermarketSuggestionsUseCase(get()) }
    factory { GetCategoriesUseCase(get()) }
    factory { GetPriceHistoryUseCase(get(), get()) }
    factory { ProductListOperationsUseCase(get(), get()) }
    factory { ProductOperationsUseCase(get(), get(), get()) }
    factory { MonthlySummaryUseCase(get()) }
    factory { RecentPurchasesUseCase(get()) }
    factory { UserProgressUseCase(get()) }

    // ViewModels
    viewModel { AuthViewModel(get(), get(), get(), get()) }
    viewModel { ProductViewModel(get(), get(), get()) }
    viewModel { ProductListViewModel(get()) }
    viewModel { InflationViewModel(get(), get(), get()) }
    viewModel { UserProfileViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
}
