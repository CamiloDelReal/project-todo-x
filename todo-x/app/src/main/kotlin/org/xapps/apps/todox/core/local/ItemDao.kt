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

    @Transaction
    @Query("SELECT * FROM items")
    fun itemsAsync(): Flow<List<Item>>

    @Transaction
    @Query("SELECT * FROM items")
    fun items(): List<Item>

    @Transaction
    @Query("SELECT * FROM items WHERE id = :id")
    fun itemAsync(id: Long): Flow<Item>

    @Transaction
    @Query("SELECT * FROM items WHERE id = :id")
    fun item(id: Long): Item

    @Transaction
    @Query("SELECT * FROM items WHERE task_id = :taskId")
    suspend fun itemsByTaskAsync(taskId: Long): List<Item>

    @Update
    suspend fun updateAsync(item: Item): Int

    @Update
    fun update(item: Item): Int

    @Update
    suspend fun updateAsync(items: List<Item>): Int

    @Update
    fun update(items: List<Item>): Int

    // The clause from inside an update sentence isn't supported yet by Room
//    @Query("UPDATE items SET done = 1 FROM (SELECT * FROM tasks WHERE tasks.category_id = 4) AS tasks_in_category WHERE task_id = tasks_in_category.id")
//    suspend fun completeByCategory(categoryId: Long): Int

    // While update-from isn't supported by room, items done field must be set given his task id
    @Query("UPDATE items SET done = 1 WHERE task_id = :taskId")
    suspend fun completeByTaskAsync(taskId: Long): Int

    // While update-from isn't supported by room, items done field must be set given his task id
    @Query("UPDATE items SET done = 1 WHERE task_id in (:tasksId)")
    suspend fun completeByTasksAsync(tasksId: List<Long>): Int

    @Delete
    suspend fun deleteAsync(item: Item): Int

    @Delete
    fun delete(item: Item): Int

    @Delete
    suspend fun deleteAsync(items: List<Item>): Int

    @Delete
    fun delete(items: List<Item>): Int

    @Query("SELECT COUNT(id) FROM items WHERE task_id = :taskId")
    suspend fun countByTaskAsync(taskId: Long): Int

    @Query("SELECT COUNT(id) FROM items WHERE task_id in (:tasksId)")
    suspend fun countByTasksAsync(tasksId: List<Long>): Int

    @Query("DELETE FROM items WHERE task_id = :taskId")
    suspend fun deleteByTaskAsync(taskId: Long): Int

    @Query("DELETE FROM items WHERE task_id in (:tasksId)")
    suspend fun deleteByTasksAsync(tasksId: List<Long>): Int

    @Query("DELETE FROM items WHERE task_id = :taskId")
    fun deleteByTask(taskId: Long): Int

}