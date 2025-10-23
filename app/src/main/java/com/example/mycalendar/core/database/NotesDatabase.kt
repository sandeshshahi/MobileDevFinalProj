package com.example.mycalendar.core.database

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mycalendar.core.database.dao.NoteDao
import com.example.mycalendar.core.database.entity.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null

//        fun getDatabase(context: Context): NotesDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val inst = Room.databaseBuilder(
//                    context.applicationContext,
//                    NotesDatabase::class.java,
//                    "notes_db"
//                ).build()
//                INSTANCE = inst
//                inst
//            }
//        }

        fun getDatabase(app: Application): NotesDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: build(app).also { INSTANCE = it }
            }

        private fun build(context: Context): NotesDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                NotesDatabase::class.java,
                "notes.db"
            )
                // Prevent crash on schema changes during development
                .fallbackToDestructiveMigration(false)
                .build()


    }
}