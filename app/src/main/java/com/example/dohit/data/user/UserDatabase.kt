package com.example.dohit.data.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [User::class], version = 2, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        // Define migration logic if schema changes occur
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Example: Add profileImageUri column to the users table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `users` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `profileImageUri` TEXT,
                        `username` TEXT NOT NULL,
                        `email` TEXT NOT NULL
                    )
                """.trimIndent())


            }
        }

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                )
                    .addMigrations(MIGRATION_1_2) // Add the migration logic
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Insert default user if no user exists
                            val defaultImageUri = "android.resource://com.example.dohit/drawable/prof_img1"
                            db.execSQL("""
                                INSERT INTO `users` (`id`, `profileImageUri`, `username`, `email`)
                                SELECT 1, '$defaultImageUri', 'Idan', 'default@example.com'
                                WHERE NOT EXISTS (SELECT 1 FROM `users`)
                            """.trimIndent())
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
