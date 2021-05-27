package org.xapps.apps.todox.core.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.xapps.apps.todox.core.models.Item


@Dao
interface ItemDao {

    @Insert
    suspend fun insertAsync(item: Item): Long

    @Insert
    fun insert(item: Item): Long

    @Insert
    suspend fun insertAsync(items: List<Item>): List<Long>

    @Insert
    fun insert(items: List<Item>): List<Long>

    @Query("SELECT * FROM items")
    fun itemsAsync(): Flow<List<Item>>

    @Query("SELECT * FROM items")
    fun items(): List<Item>

    @Query("SELECT * FROM items WHERE id = :id")
    fun itemAsync(id: Long): Flow<Item>

    @Query("SELECT * FROM items WHERE id = :id")
    fun item(id: Long): Item

    @Update
    suspend fun updateAsync(item: Item): Int

    @Update
    fun update(item: Item): Int

    @Update
    suspend fun updateAsync(items: List<Item>): Int

    @Update
    fun update(items: List<Item>): Int

    @Delete
    suspend fun deleteAsync(item: Item): Int

    @Delete
    fun delete(item: Item): Int

    @Delete
    suspend fun deleteAsync(items: List<Item>): Int

    @Delete
    fun delete(items: List<Item>): Int

    @Transaction
    @Query("DELETE FROM items WHERE task_id = :taskId")
    suspend fun deleteByTaskAsync(taskId: Long): Int

    @Transaction
    @Query("DELETE FROM items WHERE task_id = :taskId")
    fun deleteByTask(taskId: Long): Int

}