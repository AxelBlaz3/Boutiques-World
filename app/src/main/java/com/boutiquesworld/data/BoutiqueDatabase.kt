package com.boutiquesworld.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.boutiquesworld.model.Product
import com.boutiquesworld.model.Retailer

@Database(entities = [Product::class, Retailer::class], version = 1)
abstract class BoutiqueDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun retailerDao(): RetailerDao
}