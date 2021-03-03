package org.xapps.apps.todox.core.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.xapps.apps.todox.core.local.TaskDao
import org.xapps.apps.todox.core.models.Task
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

}