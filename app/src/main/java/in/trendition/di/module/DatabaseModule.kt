package `in`.trendition.di.module

import `in`.trendition.data.BoutiqueDatabase
import `in`.trendition.data.CartDao
import `in`.trendition.data.ProductDao
import `in`.trendition.data.RetailerDao
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

/**
 * Hilt module for providing the instances of objects related to the Database.
 */
@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    /**
     * Provides single BoutiqueDatabase instance throughout the app's lifecycle.
     * @return []BoutiqueDatabase]
     */
    @Singleton
    @Provides
    fun provideBoutiqueDatabase(@ApplicationContext context: Context): BoutiqueDatabase {
        return Room.databaseBuilder(
            context,
            BoutiqueDatabase::class.java,
            "boutique.db"
        ).build()
    }

    /**
     * Provides a single instance of ProductDao throughout the app's lifecycle
     * @return []ProductDao]
     */
    @Singleton
    @Provides
    fun provideProductDao(boutiqueDatabase: BoutiqueDatabase): ProductDao {
        return boutiqueDatabase.productDao()
    }

    /**
     * Provides a single instance of RetailerDao throughout the app's lifecycle
     * @return [RetailerDao]
     */
    @Singleton
    @Provides
    fun provideRetailerDao(boutiqueDatabase: BoutiqueDatabase): RetailerDao {
        return boutiqueDatabase.retailerDao()
    }

    /**
     * Provides a single instance of CartDao throughout the app's lifecycle
     * @return [CartDao]
     */
    @Singleton
    @Provides
    fun provideCartDao(boutiqueDatabase: BoutiqueDatabase): CartDao {
        return boutiqueDatabase.cartDao()
    }
}