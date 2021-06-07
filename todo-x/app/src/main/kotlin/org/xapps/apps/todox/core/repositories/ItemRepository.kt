package org.xapps.apps.todox.core.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.xapps.apps.todox.core.local.ItemDao
import org.xapps.apps.todox.core.models.Item
import org.xapps.apps.todox.core.repositories.failures.ItemFailure
import org.xapps.apps.todox.core.utils.*
import javax.inject.Inject


class ItemRepository @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val itemDao: ItemDao
) {

    suspend fun insert(items: List<Item>): Either<ItemFailure, List<Item>> = withContext(dispatcher) {
        info<ItemRepository>("Insert $items")
        try {
            val ids = itemDao.insertAsync(items)
            if(ids.size == items.size) {
                items.forEachIndexed { index, item -> item.id = ids[index] }
                info<ItemRepository>("Items inserted successfully $items")
                items.toSuccess()
            } else {
                ItemFailure.Database.toError()
            }
        } catch(ex: Exception) {
            error<ItemRepository>(ex, "Exception captured")
            ItemFailure.Exception(ex.localizedMessage).toError()
        }
    }

    suspend fun update(item: Item): Either<ItemFailure, Item> = withContext(dispatcher) {
        info<ItemRepository>("Update $item")
        try {
            val count = itemDao.updateAsync(item)
            if(count == 1) {
                info<ItemRepository>("Item updated successfully $item")
                item.toSuccess()
            } else {
                ItemFailure.Database.toError()
            }
        } catch(ex: Exception) {
            error<ItemRepository>(ex, "Exception captured")
            ItemFailure.Exception(ex.localizedMessage).toError()
        }
    }

    suspend fun completeByTask(taskId: Long): Either<ItemFailure, Boolean> = withContext(dispatcher) {
        info<ItemRepository>("Complete by task $taskId")
        try {
            val count = itemDao.countByTaskAsync(taskId)
            val success = itemDao.completeByTaskAsync(taskId)
            if(count == success){
                true.toSuccess()
            } else {
                ItemFailure.Database.toError()
            }
        } catch (ex: Exception) {
            error<ItemRepository>(ex, "Exception captured")
            ItemFailure.Exception(ex.localizedMessage).toError()
        }
    }

    suspend fun completeByTasks(tasksId: List<Long>): Either<ItemFailure, Boolean> = withContext(dispatcher) {
        info<ItemRepository>("Complete by tasks $tasksId")
        try {
            val count = itemDao.countByTasksAsync(tasksId)
            val success = itemDao.completeByTasksAsync(tasksId)
            if(count == success){
                true.toSuccess()
            } else {
                ItemFailure.Database.toError()
            }
        } catch (ex: Exception) {
            error<ItemRepository>(ex, "Exception captured")
            ItemFailure.Exception(ex.localizedMessage).toError()
        }
    }

    suspend fun deleteByTask(taskId: Long): Either<ItemFailure, Boolean> = withContext(dispatcher) {
        info<ItemRepository>("Delete by task $taskId")
        try {
            val count = itemDao.countByTaskAsync(taskId)
            val success = itemDao.deleteByTaskAsync(taskId)
            if(count == success){
                true.toSuccess()
            } else {
                ItemFailure.Database.toError()
            }
        } catch (ex: Exception) {
            error<ItemRepository>(ex, "Exception captured")
            ItemFailure.Exception(ex.localizedMessage).toError()
        }
    }

    suspend fun deleteByTasks(tasksId: List<Long>): Either<ItemFailure, Boolean> = withContext(dispatcher) {
        info<ItemRepository>("Delete by tasks $tasksId")
        try {
            val count = itemDao.countByTasksAsync(tasksId)
            val success = itemDao.deleteByTasksAsync(tasksId)
            if(count == success){
                true.toSuccess()
            } else {
                ItemFailure.Database.toError()
            }
        } catch (ex: Exception) {
            error<ItemRepository>(ex, "Exception captured")
            ItemFailure.Exception(ex.localizedMessage).toError()
        }
    }

}