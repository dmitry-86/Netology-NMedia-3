package ru.netology.nmedia.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM PostEntity WHERE viewed = 1 ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()

    @Query("""
        UPDATE PostEntity SET
               likes = likes + 1,
               likedByMe = 1
           WHERE id = :id AND likedByMe = 0;
        """)
    suspend fun likeById(id: Long)

    @Query(
        """
           UPDATE PostEntity SET
               likes = likes - 1,
               likedByMe = 0
           WHERE id = :id AND likedByMe = 1;
        """)
    suspend fun dislikeById(id: Long)

    @Query("UPDATE PostEntity SET viewed = 1 WHERE viewed = 0")
    suspend fun loadNewPosts()

}
