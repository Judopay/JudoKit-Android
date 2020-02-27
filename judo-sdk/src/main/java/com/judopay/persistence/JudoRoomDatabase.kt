package com.judopay.persistence

import android.content.Context
import androidx.room.*
import com.judopay.persistence.dao.TokenizedCardDao
import com.judopay.persistence.entity.TokenizedCardEntity

@Database(entities = [TokenizedCardEntity::class],
        version = 1,
        exportSchema = false)
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
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
