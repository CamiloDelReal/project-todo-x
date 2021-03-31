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
import javax.inject.Inject


class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {

    fun insert(task: Task): Flow<Task> {
        return flow {
            val id = taskDao.insert(task)
            task.id = id
            emit(task)
        }.flowOn(Dispatchers.IO)
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