package com.javips02.on.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.javips02.on.persistence.user.User
import com.javips02.on.persistence.user.UserDao

// Define the Room database
@Database(entities = [User::class], version = 2) // Changed version to 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        // Singleton pattern to ensure only one instance of the database is created
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Define a migration from version 1 to 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the username column and enforce uniqueness.
                database.execSQL("ALTER TABLE users ADD COLUMN username TEXT NOT NULL DEFAULT ''")
                database.execSQL("CREATE UNIQUE INDEX index_users_username ON users (username)")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add the migration here
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}