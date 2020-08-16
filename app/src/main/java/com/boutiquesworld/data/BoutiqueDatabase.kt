package com.boutiquesworld.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.boutiquesworld.model.BaseProduct
import com.boutiquesworld.model.Cart
import com.boutiquesworld.model.Retailer

@Database(
    entities = [Cart::class, BaseProduct.Fabric::class, BaseProduct.Product::class, Retailer::class],
    version = 1,
    exportSchema = false
)
abstract class BoutiqueDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun retailerDao(): RetailerDao
    abstract fun cartDao(): CartDao
}