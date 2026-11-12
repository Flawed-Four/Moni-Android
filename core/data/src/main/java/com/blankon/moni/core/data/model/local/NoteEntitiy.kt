package com.blankon.moni.core.data.model.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Fts4
import androidx.room.FtsOptions
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/** Room stores enums by name (TEXT) automatically, so no TypeConverter is needed. */
enum class NoteType { TEXT, LIST }

/** PENDING = has local changes the server hasn't seen yet. SYNCED = matches server. */
enum class SyncState { SYNCED, PENDING }

/**
 * One table for both plain notes and lists (matches PRD 6.8: notes.type = text | list).
 *
 * [id] is a LOCAL UUID, so the app can save offline immediately.
 * [serverId] is filled once the backend responds (e.g. "lst_4c9e1a").
 * Times are epoch millis (UTC); the API uses ISO-8601 strings, converted in the mappers.
 */
@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["server_id"], unique = true), // multiple NULLs are allowed in SQLite
        Index(value = ["updated_at"]),
        Index(value = ["deleted_at"]),
    ],
)


data class NoteEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "server_id") val serverId: String? = null,
    val type: NoteType,
    val title: String,
    /** Only used when type == TEXT. */
    val body: String? = null,
    val version: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    /** Soft delete (F8). Non-null = deleted. Kept so deletion can sync to other devices. */
    @ColumnInfo(name = "deleted_at") val deletedAt: Long? = null,
    @ColumnInfo(name = "sync_state") val syncState: SyncState = SyncState.PENDING,
    /** UUID sent as client_request_id so a retried POST is never processed twice. */
    @ColumnInfo(name = "client_request_id") val clientRequestId: String? = null,
)


@Entity(
    tableName = "list_items",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["note_id"])],
)
data class ListItemEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "note_id") val noteId: String,
    val name: String,
    /** Nullable on purpose: the LLM must not invent quantities (PRD 6.3). */
    val qty: Double? = null,
    val unit: String? = null,
    val category: String? = null,
    val checked: Boolean = false,
    val position: Int,
)


/**
 * Full-text search (F9). These are "external content" FTS tables: they index the
 * columns of the tables above and Room keeps them in sync with triggers.
 * Column names MUST match the content entity's column names.
 */
@Fts4(contentEntity = NoteEntity::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity(tableName = "notes_fts")
data class NoteFtsEntity(
    val title: String,
    val body: String?,
)


@Fts4(contentEntity = ListItemEntity::class, tokenizer = FtsOptions.TOKENIZER_UNICODE61)
@Entity(tableName = "list_items_fts")
data class ListItemFtsEntity(
    val name: String,
)

/** A note with its items, for the list detail screen. Relation results are NOT ordered. */
data class NoteWithItems(
    @Embedded val note: NoteEntity,
    @Relation(parentColumn = "id", entityColumn = "note_id")
    val items: List<ListItemEntity>,
)