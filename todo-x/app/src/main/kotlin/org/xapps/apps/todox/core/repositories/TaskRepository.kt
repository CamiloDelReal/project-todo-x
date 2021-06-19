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
import org.xapps.apps.todox.core.models.TaskAndCategory
import org.xapps.apps.todox.core.models.TaskWithItems
import org.xapps.apps.todox.core.models.TaskWithItemsAndCategory
import org.xapps.apps.todox.core.repositories.failures.TaskFailure
import org.xapps.apps.todox.core.utils.*
import org.xapps.apps.todox.viewmodels.Constants
import timber.log.Timber
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject


class TaskRepository @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val taskDao: TaskDao,
    private val itemRepository: ItemRepository
) {

    suspend fun insertWithItems(taskWithItems: TaskWithItems): Either<TaskFailure, TaskWithItems> = withContext(dispatcher) {
        info<TaskRepository>("Insert $taskWithItems")
        try {
            val taskId = taskDao.insertAsync(taskWithItems.task)
            if (taskId != Constants.ID_INVALID) {
                taskWithItems.task.id = taskId
                taskWithItems.items.forEach { it.taskId = taskId }
                val itemsSuccess = itemRepository.insert(taskWithItems.items)
                if(itemsSuccess.isSuccess) {
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

    suspend fun updateWithItems(taskWithItems: TaskWithItems): Either<TaskFailure, TaskWithItems> = withContext(dispatcher) {
        info<TaskRepository>("Update $taskWithItems")
        try {
            val taskSuccess = taskDao.updateAsync(taskWithItems.task)
            if (taskSuccess == 1) {
                taskWithItems.items.forEach { it.taskId = taskWithItems.task.id }
                val itemsDeleteSuccess = itemRepository.deleteByTask(taskWithItems.task.id)
                if(itemsDeleteSuccess.isSuccess) {
                    val itemsInsertSuccess = itemRepository.insert(taskWithItems.items)
                    if(itemsInsertSuccess.isSuccess) {
                        taskWithItems.toSuccess()
                    } else {
                        TaskFailure.Database.toError()
                    }
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

    suspend fun complete(task: Task): Either<TaskFailure, Task> = withContext(dispatcher) {
        info<TaskRepository>("Complete $task")
        try {
            val itemsSuccess = itemRepository.completeByTask(task.id)
            if(itemsSuccess.isSuccess) {
                task.done = true
                val count = taskDao.updateAsync(task)
                if(count == 1) {
                    info<TaskRepository>("Task updated successfully $task")
                    task.toSuccess()
                } else {
                    TaskFailure.Database.toError()
                }
            } else {
                TaskFailure.Database.toError()
            }
        } catch(ex: Exception) {
            error<TaskRepository>(ex, "Exception captured")
            TaskFailure.Exception(ex.localizedMessage).toError()
        }
    }

    suspend fun uncomplete(task: Task): Either<TaskFailure, Task> = withContext(dispatcher) {
        info<TaskRepository>("Uncomplete $task")
        try {
            task.done = false
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

    suspend fun completeTasksInCategory(categoryId: Long): Either<TaskFailure, Boolean> = withContext(dispatcher) {
        info<TaskRepository>("Complete all tasks of category $categoryId")
        try {
            val tasksId = taskDao.tasksIdByCategoryAsync(categoryId)
            val itemsResult = itemRepository.completeByTasks(tasksId)
            if(itemsResult.isSuccess) {
                val tasksUpdated = taskDao.completeByCategoryAsync(categoryId)
                if(tasksUpdated == tasksId.size) {
                    true.toSuccess()
                } else {
                    TaskFailure.Database.toError()
                }
            } else {
                TaskFailure.Database.toError()
            }
        } catch (ex: Exception) {
            error<TaskRepository>(ex, "Exception captured")
            TaskFailure.Exception(ex.localizedMessage).toError()
        }
    }

    suspend fun deleteTasksInCategory(categoryId: Long): Either<TaskFailure, Boolean> = withContext(dispatcher) {
        info<TaskRepository>("Delete all tasks of category $categoryId")
        try {
            val tasksId = taskDao.tasksIdByCategoryAsync(categoryId)
            val itemsResult = itemRepository.deleteByTasks(tasksId)
            if(itemsResult.isSuccess){
                val tasksDeleted = taskDao.deleteByCategoryAsync(categoryId)
                if(tasksDeleted == tasksId.size) {
                    true.toSuccess()
                } else {
                    TaskFailure.Database.toError()
                }
            } else {
                TaskFailure.Database.toError()
            }
        } catch (ex: Exception) {
            error<TaskRepository>(ex, "Exception captured")
            TaskFailure.Exception(ex.localizedMessage).toError()
        }
    }

    suspend fun delete(task: Task): Either<TaskFailure, Task> = withContext(dispatcher) {
        info<TaskRepository>("Delete $task")
        try {
            val itemsSuccess = itemRepository.deleteByTask(task.id)
            if(itemsSuccess.isSuccess) {
                val count = taskDao.delete(task)
                if(count == 1) {
                    task.toSuccess()
                } else {
                    TaskFailure.Database.toError()
                }
            } else {
                TaskFailure.Database.toError()
            }
        } catch (ex: Exception) {
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

    fun tasksInScheduleAndCategoryByMonth(yearMonth: YearMonth): Flow<List<TaskAndCategory>> {
        debug<TaskRepository>("Formatting $yearMonth\n\t${yearMonth.parseToString(Constants.YEAR_MONTH_PATTERN)}")
        return taskDao.tasksInScheduleWithItemsAndCategoryByMonthPaginatedAsync(yearMonth.parseToString(Constants.YEAR_MONTH_PATTERN)).flowOn(dispatcher)
    }

    fun tasksInScheduleWithItemsAndCategoryByDatePaginated(date: LocalDate): Flow<PagingData<TaskWithItemsAndCategory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksInScheduleWithItemsAndCategoryByDatePaginatedAsync(date.parseToString(Constants.DATE_PATTERN_DB))
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

    suspend fun moveToUnclassified(categoryId: Long): Either<TaskFailure, Boolean> = withContext(dispatcher) {
        info<TaskRepository>("Move tasks of category $categoryId to unclassified")
        try {
            val ids = taskDao.tasksIdByCategoryAsync(categoryId)
            val success = taskDao.changeCategoryAsync(categoryId, Constants.UNCLASSIFED_CATEGORY_ID)
            if(success == ids.size) {
                true.toSuccess()
            } else {
                TaskFailure.Database.toError()
            }
        } catch (ex: Exception) {
            error<TaskRepository>(ex, "Exception captured")
            TaskFailure.Exception(ex.localizedMessage).toError()
        }
    }

}