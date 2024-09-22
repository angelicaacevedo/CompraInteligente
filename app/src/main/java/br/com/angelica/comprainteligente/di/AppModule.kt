package br.com.angelica.comprainteligente.di

import br.com.angelica.comprainteligente.presentation.viewmodel.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { LoginViewModel() }
}
