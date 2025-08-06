package com.judopay.judokit.android.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.judopay.judokit.android.db.dao.TokenizedCardDao
import com.judopay.judokit.android.db.entity.TokenizedCardEntity

@Database(
    entities = [TokenizedCardEntity::class],
    version = 3,
    exportSchema = false,
)
@TypeConverters(JudoTypeConverters::class)
abstract class JudoRoomDatabase : RoomDatabase() {
    abstract fun tokenizedCardDao(): TokenizedCardDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var database: JudoRoomDatabase? = null

        fun getDatabase(context: Context): JudoRoomDatabase {
            val tempInstance = database
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance =
                    Room
                        .databaseBuilder(
                            context.applicationContext,
                            JudoRoomDatabase::class.java,
                            "judo_database",
                        ).fallbackToDestructiveMigration(false)
                        .build()
                database = instance
                return instance
            }
        }
    }
}
