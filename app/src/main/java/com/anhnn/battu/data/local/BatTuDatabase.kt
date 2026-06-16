package com.anhnn.battu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SuKienEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class BatTuDatabase : RoomDatabase() {

    abstract fun suKienDao(): SuKienDao

    companion object {
        @Volatile
        private var INSTANCE: BatTuDatabase? = null

        fun getInstance(context: Context): BatTuDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    BatTuDatabase::class.java,
                    "battu_database",
                ).build().also { INSTANCE = it }
            }
    }
}
