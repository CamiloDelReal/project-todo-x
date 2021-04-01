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
import org.xapps.apps.todox.core.models.TaskWithItems
import timber.log.Timber
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

    fun tasksScheduledPaginated(categoryId: Long): Flow<PagingData<TaskWithItems>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksScheduledWithItemsPaginatedAsync(categoryId)
        }.flow.flowOn(Dispatchers.IO)
    }

    fun tasksImportantPaginated(categoryId: Long): Flow<PagingData<TaskWithItems>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksImportantWithItemsPaginatedAsync(categoryId)
        }.flow.flowOn(Dispatchers.IO)
    }

    fun tasksCompletedPaginated(categoryId: Long): Flow<PagingData<TaskWithItems>> {
        return Pager (
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            taskDao.tasksCompletedWithItemsPaginatedAsync(categoryId)
        }.flow.flowOn(Dispatchers.IO)
    }

}