package org.xapps.apps.todox.core.local

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskAndCategory
import org.xapps.apps.todox.core.models.TaskWithItems


@Dao
interface TaskDao {

    @Insert
    suspend fun insertAsync(task: Task): Long

    @Insert
    fun insert(task: Task): Long

    @Insert
    suspend fun insertAsync(tasks: List<Task>): List<Long>

    @Insert
    fun insert(tasks: List<Task>): List<Long>

    @Query("SELECT * FROM tasks")
    fun tasksAsync(): Flow<List<Task>>

    @Query("SELECT * FROM tasks")
    fun tasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun taskAsync(id: Long): Flow<Task>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun task(id: Long): Task

    @Transaction
    @Query("SELECT * FROM tasks")
    fun tasksAndCategoryAsync(): Flow<List<TaskAndCategory>>

    @Transaction
    @Query("SELECT * FROM tasks")
    fun tasksAndCategory(): List<TaskAndCategory>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun taskAndCategoryAsync(id: Long): Flow<TaskAndCategory>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun taskAndCategory(id: Long): TaskAndCategory

    @Transaction
    @Query("SELECT * FROM tasks")
    fun tasksWithItemsAsync(): Flow<List<TaskWithItems>>

    @Transaction
    @Query("SELECT * FROM tasks")
    fun tasksWithItems(): List<TaskWithItems>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun taskWithItemsAsync(id: Long): Flow<TaskWithItems>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :id")
    fun taskWithItems(id: Long): List<TaskWithItems>

    @Transaction
    @Query("SELECT * FROM tasks WHERE category_id = :categoryId AND done = 0")
    fun tasksScheduledWithItemsPaginatedAsync(categoryId: Long): PagingSource<Int, TaskWithItems>

    @Transaction
    @Query("SELECT * FROM tasks WHERE category_id = :categoryId AND done = 0 AND important = 1")
    fun tasksImportantWithItemsPaginatedAsync(categoryId: Long): PagingSource<Int, TaskWithItems>

    @Transaction
    @Query("SELECT * FROM tasks WHERE category_id = :categoryId AND done = 1")
    fun tasksCompletedWithItemsPaginatedAsync(categoryId: Long): PagingSource<Int, TaskWithItems>

    @Update
    suspend fun updateAsync(task: Task): Int

    @Update
    fun update(task: Task): Int

    @Update
    suspend fun updateAsync(tasks: List<Task>): Int

    @Update
    fun update(tasks: List<Task>): Int

    @Delete
    suspend fun deleteAsync(task: Task): Int

    @Delete
    fun delete(task: Task): Int

    @Delete
    suspend fun deleteAsync(tasks: List<Task>): Int

    @Delete
    fun delete(tasks: List<Task>): Int


}