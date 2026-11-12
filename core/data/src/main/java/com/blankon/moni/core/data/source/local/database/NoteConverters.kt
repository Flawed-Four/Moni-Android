package com.blankon.moni.core.data.source.local.database


import androidx.room.TypeConverter
import com.blankon.moni.core.data.model.local.NoteType
import com.blankon.moni.core.data.model.local.SyncState

class NoteConverters {
    @TypeConverter
    fun fromNoteType(value: NoteType): String = value.name

    @TypeConverter
    fun toNoteType(value: String): NoteType = NoteType.valueOf(value)

    @TypeConverter
    fun fromSyncState(value: SyncState): String = value.name

    @TypeConverter
    fun toSyncState(value: String): SyncState = SyncState.valueOf(value)
}
