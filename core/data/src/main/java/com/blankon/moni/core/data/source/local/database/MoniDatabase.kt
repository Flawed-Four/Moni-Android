package com.blankon.moni.core.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.blankon.moni.core.data.model.local.ListItemEntity
import com.blankon.moni.core.data.model.local.ListItemFtsEntity
import com.blankon.moni.core.data.model.local.NoteEntity
import com.blankon.moni.core.data.model.local.NoteFtsEntity
import com.blankon.moni.core.data.source.local.dao.ListItemDao
import com.blankon.moni.core.data.source.local.dao.NoteDao

@Database(
    entities = [
        NoteEntity::class,
        ListItemEntity::class,
        NoteFtsEntity::class,
        ListItemFtsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(NoteConverters::class)
abstract class MoniDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun listItemDao(): ListItemDao

    companion object {
        const val DATABASE_NAME = "moni_database"
    }
}
