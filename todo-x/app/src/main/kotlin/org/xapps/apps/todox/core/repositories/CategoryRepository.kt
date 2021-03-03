package org.xapps.apps.todox.core.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.xapps.apps.todox.core.local.CategoryDao
import org.xapps.apps.todox.core.models.Category
import timber.log.Timber
import javax.inject.Inject


class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {

    fun insert(categories: List<Category>): Flow<Boolean> {
        return flow {
            val ids = categoryDao.insert(categories)
            emit(ids.size == categories.size)
        }.flowOn(Dispatchers.IO)
    }

    fun categoriesPaginated(): Flow<PagingData<Category>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            categoryDao.categoriesPaginated()
        }.flow.flowOn(Dispatchers.IO)
    }

    fun categories(): Flow<List<Category>> {
        Timber.i("About to call categoriesAsync")
        return categoryDao.categoriesAsync()
            .flowOn(Dispatchers.IO)
    }

    fun category(id: Long): Flow<Category> {
        return categoryDao.categoryAsync(id)
            .flowOn(Dispatchers.IO)
    }

}