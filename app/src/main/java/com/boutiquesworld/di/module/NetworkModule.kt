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

/**
 * Hilt module for providing the instances of objects related to network
 */
@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    /**
     * Provides a single instance of Retrofit object throughout app's lifecycle.
     * @return Retrofit
     */
    @Singleton
    @Provides
    fun provideRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides a single instance of BoutiqueService throughout the app's lifecycle.
     * @param retrofit: Retrofit instance obtained from [provideRetrofitInstance]
     * @return BoutiqueService
     */
    @Singleton
    @Provides
    fun provideBoutiqueService(retrofit: Retrofit): BoutiqueService {
        return retrofit.create(BoutiqueService::class.java)
    }
}