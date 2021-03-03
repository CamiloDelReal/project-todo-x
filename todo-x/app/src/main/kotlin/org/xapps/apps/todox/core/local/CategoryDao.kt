package org.xapps.apps.todox.core.local

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.models.CategoryWithTasks


@Dao
interface CategoryDao {

    @Insert
    suspend fun insertAsync(category: Category): Long

    @Insert
    fun insert(category: Category): Long

    @Insert
    suspend fun insertAsync(categories: List<Category>): List<Long>

    @Insert
    fun insert(categories: List<Category>): List<Long>

    @Query("SELECT * FROM categories")
    fun categoriesAsync(): Flow<List<Category>>

    @Query("SELECT * FROM categories")
    fun categories(): List<Category>

//    @Query("SELECT * FROM categories WHERE id = :id")
    @Query("SELECT categories.id, categories.name, categories.color, COUNT(tasks.id) as tasks_count, COUNT(tasks.id) as pending_tasks_count FROM categories LEFT JOIN tasks on (categories.id = tasks.category_id)  WHERE categories.id = :id GROUP BY categories.id ORDER BY categories.name ASC")
    fun categoryAsync(id: Long): Flow<Category>

//    @Query("SELECT * FROM categories WHERE id = :id")
    @Query("SELECT categories.id, categories.name, categories.color, COUNT(tasks.id) as tasks_count, COUNT(tasks.id) as pending_tasks_count FROM categories LEFT JOIN tasks on (categories.id = tasks.category_id)  WHERE categories.id = :id GROUP BY categories.id ORDER BY categories.name ASC")
    fun category(id: Long): Category

    @Query("SELECT categories.id, categories.name, categories.color, COUNT(tasks.id) as tasks_count, COUNT(tasks.id) as pending_tasks_count FROM categories LEFT JOIN tasks on (categories.id = tasks.category_id) GROUP BY categories.id ORDER BY categories.name ASC")
    fun categoriesPaginated(): PagingSource<Int, Category>

    @Transaction
    @Query("SELECT * FROM categories")
    fun categoriesWithTasksAsync(): Flow<List<CategoryWithTasks>>

    @Transaction
    @Query("SELECT * FROM categories")
    fun categoriesWithTasks(): List<CategoryWithTasks>

    @Transaction
    @Query("SELECT * FROM categories WHERE id = :id")
    fun categoryWithTasksAsync(id: Long): Flow<CategoryWithTasks>

    @Transaction
    @Query("SELECT * FROM categories WHERE id = :id")
    fun categoryWithTasks(id: Long): CategoryWithTasks

    @Update
    suspend fun updateAsync(category: Category): Int

    @Update
    fun update(category: Category): Int

    @Update
    suspend fun updateAsync(categories: List<Category>): Int

    @Update
    fun update(categories: List<Category>): Int

    @Delete
    suspend fun deleteAsync(category: Category): Int

    @Delete
    fun delete(category: Category): Int

    @Delete
    suspend fun deleteAsync(categories: List<Category>): Int

    @Delete
    fun delete(categories: List<Category>): Int

}