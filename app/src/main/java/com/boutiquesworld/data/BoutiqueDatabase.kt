package com.boutiquesworld.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.boutiquesworld.model.*

@Database(
    entities = [Cart::class, BaseProduct.Store::class, BaseProduct.Product::class, Retailer::class, Address::class, Order::class, BaseProduct.Sketch::class, OrderAddress::class, BoutiqueRequest::class, Subscription::class, SubscriptionPlan::class, Payment::class, StoreCategory.Jewellery::class, StoreCategory.Cloth::class, StoreCategory.Fabric::class, StoreCategory.DressMaterial::class],
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
    abstract fun boutiqueRequestDao(): BoutiqueRequestDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun paymentDao(): PaymentDao
    abstract fun storeCategoryDao(): StoreCategoryDao
}