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

}