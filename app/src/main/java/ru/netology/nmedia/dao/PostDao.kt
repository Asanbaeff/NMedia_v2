package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.enumeration.AttachmentType

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity WHERE hidden = 0 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT COUNT(*) FROM PostEntity")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("SELECT * FROM PostEntity WHERE id = :id LIMIT 1")
    suspend fun getPostById(id: Long): PostEntity?

    @Query("SELECT COUNT(*) FROM PostEntity WHERE hidden = 1")
    fun getHiddenCount(): Flow<Int>

    @Query("UPDATE PostEntity SET hidden = 0 WHERE hidden = 1")
    suspend fun unhideAll()

    @Query("UPDATE PostEntity SET hidden = 0 WHERE hidden = 1")
    suspend fun showAll()

    @Query("SELECT MAX(id) FROM PostEntity WHERE hidden = 1")
    suspend fun maxIdShadow(): Long?

    @Query("SELECT MAX(id) FROM PostEntity")
    suspend fun maxId(): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertShadow(posts: List<PostEntity>)

    @Query("SELECT MAX(id) FROM PostEntity WHERE hidden = 0")
    suspend fun maxIdVisible(): Long?

}

@TypeConverters
class Converter {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)

    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
}
