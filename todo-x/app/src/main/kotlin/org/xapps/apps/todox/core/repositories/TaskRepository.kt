package org.xapps.apps.todox.core.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import org.xapps.apps.todox.core.local.ItemDao
import org.xapps.apps.todox.core.local.TaskDao
import org.xapps.apps.todox.core.models.Task
import org.xapps.apps.todox.core.models.TaskWithItems
import org.xapps.apps.todox.core.models.TaskWithItemsAndCategory
import org.xapps.apps.todox.core.repositories.failures.TaskFailure
import org.xapps.apps.todox.core.utils.*
import org.xapps.apps.todox.viewmodels.Constants
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject


class TaskRepository @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val taskDao: TaskDao,
    private val itemDao: ItemDao
) {

    suspend fun insertTaskWithItems(taskWithItems: TaskWithItems): Either<TaskFailure, TaskWithItems> = withContext(dispatcher) {
        info<TaskRepository>("Insert $taskWithItems")
        try {
            val taskId = taskDao.insertAsync(taskWithItems.task)
            if (taskId != Constants.ID_INVALID) {
                taskWithItems.task.id = taskId
                taskWithItems.items.forEach { it.taskId = taskId }
                val itemsId = itemDao.insertAsync(taskWithItems.items)
                if(itemsId.size == taskWithItems.items.size) {
                    taskWithItems.items.forEachIndexed { index, item -> item.id = itemsId[index] }
                    taskWithItems.toSuccess()
                } else {
                    TaskFailure.Database.toError()
                }
            } else {
                TaskFailure.Database.toError()
            }
        } catch (ex: Exception) {
            TaskFailure.Exception(ex.localizedMessage).toError()
        }
    }

    suspend fun update(task: Task): Either<TaskFailure, Task> = withContext(dispatcher) {
        info<TaskRepository>("Update $task")
        try {
            val count = taskDao.updateAsync(task)
            if(count == 1) {
                info<TaskRepository>("Task updated successfully $task")
                task.toSuccess()
            } else {
                TaskFailure.Database.toError()
            }
        } catch(ex: Exception) {
            error<TaskRepository>(ex, "Exception captured")
            TaskFailure.Exception(ex.localizedMessage).toError()
        }
    }

    fun taskWithItems(id: Long): Flow<TaskWithItems> {
        Timber.i("Get task $id with items")
        return taskDao.taskWithItemsAsync(id).flowOn(dispatcher)
    }

    fun taskWithItemsAndCategory(id: Long): Flow<TaskWithItemsAndCategory> {
        Timber.i("Get task $id with items and category")
        return taskDao.taskWithItemsAndCategoryAsync(id).flowOn(dispatcher)
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
        }.flow.flowOn(dispatcher)
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
        }.flow.flowOn(dispatcher)
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
        }.flow.flowOn(dispatcher)
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
        }.flow.flowOn(dispatcher)
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
        }.flow.flowOn(dispatcher)
    }

    fun tasksCompletedWithItemsByCategoryPaginated(categoryId: Long): Flow<PagingData<TaskWithItems>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksCompletedWithItemsByCategoryPaginatedAsync(categoryId)
        }.flow.flowOn(dispatcher)
    }

    fun tasksCompletedWithItemsAndCategoryPaginated(): Flow<PagingData<TaskWithItemsAndCategory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksCompletedWithItemsAndCategoryPaginatedAsync()
        }.flow.flowOn(dispatcher)
    }

    fun tasksTodayWithItemsAndCategoryPaginated(): Flow<PagingData<TaskWithItemsAndCategory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksForDateWithItemsAndCategoryPaginatedAsync(LocalDate.now().parseToString(Constants.DATE_PATTERN_DB))
        }.flow.flowOn(dispatcher)
    }

    fun tasksImportantCount(): Flow<Int> {
        return taskDao.tasksImportantCountAsync().flowOn(dispatcher)
    }

    fun tasksInScheduleCount(): Flow<Int> {
        return taskDao.tasksInScheduleCountAsync().flowOn(dispatcher)
    }

    fun tasksTodayCount(): Flow<Int> {
        return taskDao.tasksForDateCount(LocalDate.now().parseToString(Constants.DATE_PATTERN_DB))
            .flowOn(dispatcher)
    }

}