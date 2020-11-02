package org.xapps.apps.todox.services.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.xapps.apps.todox.services.models.Task
import org.xapps.apps.todox.services.models.TaskAndCategory


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