package com.boutiquesworld.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.boutiquesworld.model.*

@Database(
    entities = [Cart::class, BaseProduct.Store::class, BaseProduct.Product::class, Retailer::class, Address::class, Order::class, BaseProduct.Sketch::class],
    version = 1,
    exportSchema = false
)
abstract class BoutiqueDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun retailerDao(): RetailerDao
    abstract fun cartDao(): CartDao
    abstract fun addressDao(): AddressDao
    abstract fun orderDao(): OrderDao
    abstract fun sketchDao(): SketchDao
}