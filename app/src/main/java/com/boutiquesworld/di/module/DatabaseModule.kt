package com.boutiquesworld.di.module

import android.content.Context
import androidx.room.Room
import com.boutiquesworld.data.BoutiqueDatabase
import com.boutiquesworld.data.ProductDao
import com.boutiquesworld.data.RetailerDao
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
     * @return BoutiqueDatabase
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
     * @return ProductDao
     */
    @Singleton
    @Provides
    fun provideProductDao(boutiqueDatabase: BoutiqueDatabase): ProductDao {
        return boutiqueDatabase.productDao()
    }

    /**
     * Provides a single instance of RetailerDao throughout the app's lifecycle
     * @return RetailerDao
     */
    @Singleton
    @Provides
    fun provideRetailerDao(boutiqueDatabase: BoutiqueDatabase): RetailerDao {
        return boutiqueDatabase.retailerDao()
    }
}