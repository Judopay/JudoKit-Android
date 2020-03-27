package com.judopay.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.judopay.db.dao.TokenizedCardDao
import com.judopay.db.entity.TokenizedCardEntity

@Database(
    entities = [TokenizedCardEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(JudoTypeConverters::class)
abstract class JudoRoomDatabase : RoomDatabase() {

    abstract fun tokenizedCardDao(): TokenizedCardDao

    companion object {

        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: JudoRoomDatabase? = null

        fun getDatabase(context: Context): JudoRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    JudoRoomDatabase::class.java,
                    "judo_database"
                )
                    // TODO: Write proper migrations when going live
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
