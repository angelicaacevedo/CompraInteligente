package br.com.angelica.comprainteligente.di

import br.com.angelica.comprainteligente.data.AuthRepository
import br.com.angelica.comprainteligente.data.FirebaseAuthRepository
import br.com.angelica.comprainteligente.data.FirestoreProductRepository
import br.com.angelica.comprainteligente.data.ProductRepository
import br.com.angelica.comprainteligente.domain.AddProductUseCase
import br.com.angelica.comprainteligente.domain.GetProductDetailUseCase
import br.com.angelica.comprainteligente.domain.GetProductsUseCase
import br.com.angelica.comprainteligente.domain.LoginUseCase
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
    // Use Cases
    factory { AddProductUseCase(get()) }
    factory { GetProductsUseCase(get()) }
    factory { GetProductDetailUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    // ViewModels
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { NavigationViewModel() }
    viewModel { ListsViewModel(get()) }
    viewModel { ProductDetailsViewModel(get()) }
    viewModel { AddProductViewModel(get()) }
}
