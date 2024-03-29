package `in`.trendition.di.module

import `in`.trendition.BuildConfig
import `in`.trendition.network.BoutiqueService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
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
    fun provideRetrofitInstance(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.BASE_URL)
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

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(Duration.ZERO)
            .writeTimeout(Duration.ZERO)
            .build()
    }
}