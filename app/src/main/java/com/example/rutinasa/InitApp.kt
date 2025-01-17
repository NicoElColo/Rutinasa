package com.example.rutinasa

import android.app.Application

class InitApp : Application() {
    override fun onCreate() {
        super.onCreate()

        /// Limpiar bd
//        val helper = MyDatabaseHelper(this)
//        helper.onUpgrade(helper.writableDatabase, 1, 2)

        DatabaseManager.initialize(this)

    }
}