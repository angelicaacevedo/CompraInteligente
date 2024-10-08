package br.com.angelica.comprainteligente.di

import br.com.angelica.comprainteligente.data.auth.AuthRepository
import br.com.angelica.comprainteligente.data.auth.FirebaseAuthRepository
import br.com.angelica.comprainteligente.data.price.PriceAnalyzer
import br.com.angelica.comprainteligente.data.price.PriceAnalyzerRepository
import br.com.angelica.comprainteligente.data.product.FirestoreProductRepository
import br.com.angelica.comprainteligente.data.product.ProductRepository
import br.com.angelica.comprainteligente.domain.LoginUseCase
import br.com.angelica.comprainteligente.domain.PriceAnalyzerUseCase
import br.com.angelica.comprainteligente.domain.ProductUseCase
import br.com.angelica.comprainteligente.domain.RegisterUseCase
import br.com.angelica.comprainteligente.presentation.viewmodel.AddProductViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.HomeViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.ListsViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.LoginViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.NavigationViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.ProductDetailsViewModel
import br.com.angelica.comprainteligente.presentation.viewmodel.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Repositories
    single<AuthRepository> { FirebaseAuthRepository() }
    single<ProductRepository> { FirestoreProductRepository() }
    single<PriceAnalyzer> { PriceAnalyzerRepository(get()) }


    // Use Cases
    factory { ProductUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { PriceAnalyzerUseCase(get()) }

    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { NavigationViewModel() }
    viewModel { ListsViewModel() }
    viewModel { ProductDetailsViewModel(get()) }
    viewModel { AddProductViewModel(get()) }
}
