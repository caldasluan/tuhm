package br.com.pignata.tuhm.di

import br.com.pignata.tuhm.data.database.AppDatabase
import br.com.pignata.tuhm.repository.DatabaseRepository
import br.com.pignata.tuhm.view.view_model.*
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getInstance(androidApplication()) }
    single { get<AppDatabase>().projectDao() }
    single { get<AppDatabase>().problemDao() }
}

val repositoryModule = module {
    single { DatabaseRepository(get(), get()) }
}

val viewModelModule = module {
    viewModel { AddEditProjectViewModel(get(), get()) }
    viewModel { ListProjectsViewModel(get()) }
    viewModel { ProjectViewModel(get()) }
    viewModel { AddEditProblemViewModel(get(), get()) }
    viewModel { ReportViewModel() }
    viewModel { MainViewModel() }
}

val appModules = listOf(databaseModule, repositoryModule, viewModelModule)