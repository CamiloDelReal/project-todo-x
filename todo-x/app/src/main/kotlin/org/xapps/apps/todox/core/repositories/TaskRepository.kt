package org.xapps.apps.todox.core.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.xapps.apps.todox.core.local.TaskDao
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskAndCategory
import org.xapps.apps.todox.core.models.TaskWithItems
import org.xapps.apps.todox.core.models.TaskWithItemsAndCategory
import org.xapps.apps.todox.core.utils.parseToString
import org.xapps.apps.todox.viewmodels.Constants
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {

    fun insert(task: Task): Flow<Task> {
        Timber.i("Insert task $task")
        return flow {
            val id = taskDao.insertAsync(task)
            Timber.i("Task inserted $task")
            task.id = id
            emit(task)
        }.flowOn(Dispatchers.IO)
    }

    fun update(task: Task): Flow<Boolean> {
        Timber.i("Update task $task")
        return flow {
            val count = taskDao.updateAsync(task)
            Timber.i("Task updated $task")
            emit(count == 1)
        }.flowOn(Dispatchers.IO)
    }

    fun taskWithItems(id: Long): Flow<TaskWithItems> {
        Timber.i("Get task $id with items")
        return taskDao.taskWithItemsAsync(id).flowOn(Dispatchers.IO)
    }

    fun taskWithItemsAndCategory(id: Long): Flow<TaskWithItemsAndCategory> {
        Timber.i("Get task $id with items and category")
        return taskDao.taskWithItemsAndCategoryAsync(id).flowOn(Dispatchers.IO)
    }

    fun tasksWithItemsAndCategoryPaginated(): Flow<PagingData<TaskWithItemsAndCategory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksWithItemsAndCategoryPaginatedAsync()
        }.flow.flowOn(Dispatchers.IO)
    }

    fun tasksInScheduleWithItemsByCategoryPaginated(categoryId: Long): Flow<PagingData<TaskWithItems>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksInScheduleWithItemsByCategoryPaginatedAsync(categoryId)
        }.flow.flowOn(Dispatchers.IO)
    }

    fun tasksInScheduleWithItemsAndCategoryPaginated(): Flow<PagingData<TaskWithItemsAndCategory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksInScheduleWithItemsAndCategoryPaginatedAsync()
        }.flow.flowOn(Dispatchers.IO)
    }

    fun tasksImportantWithItemsByCategoryPaginated(categoryId: Long): Flow<PagingData<TaskWithItems>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksImportantWithItemsByCategoryPaginatedAsync(categoryId)
        }.flow.flowOn(Dispatchers.IO)
    }

    fun tasksImportantWithItemsAndCategoryPaginated(): Flow<PagingData<TaskWithItemsAndCategory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksImportantWithItemsAndCategoryPaginatedAsync()
        }.flow.flowOn(Dispatchers.IO)
    }

    fun tasksCompletedWithItemsByCategoryPaginated(categoryId: Long): Flow<PagingData<TaskWithItems>> {
        return Pager (
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksCompletedWithItemsByCategoryPaginatedAsync(categoryId)
        }.flow.flowOn(Dispatchers.IO)
    }

    fun tasksCompletedWithItemsAndCategoryPaginated(): Flow<PagingData<TaskWithItemsAndCategory>> {
        return Pager (
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksCompletedWithItemsAndCategoryPaginatedAsync()
        }.flow.flowOn(Dispatchers.IO)
    }

    fun tasksTodayWithItemsAndCategoryPaginated(): Flow<PagingData<TaskWithItemsAndCategory>> {
        return Pager (
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksForDateWithItemsAndCategoryPaginatedAsync(LocalDate.now().parseToString(Constants.DATE_PATTERN_DB))
        }.flow.flowOn(Dispatchers.IO)
    }

    fun tasksImportantCount(): Flow<Int> {
        return taskDao.tasksImportantCountAsync().flowOn(Dispatchers.IO)
    }

    fun tasksInScheduleCount(): Flow<Int> {
        return taskDao.tasksInScheduleCountAsync().flowOn(Dispatchers.IO)
    }

    fun tasksTodayCount(): Flow<Int> {
        return taskDao.tasksForDateCount(LocalDate.now().parseToString(Constants.DATE_PATTERN_DB)).flowOn(Dispatchers.IO)
    }

}