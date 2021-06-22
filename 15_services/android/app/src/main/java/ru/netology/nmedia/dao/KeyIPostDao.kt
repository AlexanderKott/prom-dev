package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.entity.PostKeyEntry

@Dao
interface KeyIPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listOf: List<PostKeyEntry>)

    @Query ("SELECT MIN(id) FROM PostKeyEntry")
    fun min(): Long?

    @Query ("SELECT MAX(id) FROM PostKeyEntry")
    fun max(): Long?
}