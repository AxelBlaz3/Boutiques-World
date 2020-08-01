package com.boutiquesworld.di.module

import com.boutiquesworld.network.BoutiqueService
import com.boutiquesworld.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun provideBoutiqueService(retrofit: Retrofit): BoutiqueService {
        return retrofit.create(BoutiqueService::class.java)
    }
}