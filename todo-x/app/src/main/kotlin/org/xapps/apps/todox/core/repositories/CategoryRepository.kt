package org.xapps.apps.todox.core.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.xapps.apps.todox.core.local.CategoryDao
import org.xapps.apps.todox.core.models.Category
import org.xapps.apps.todox.core.repositories.failures.CategoryFailure
import org.xapps.apps.todox.core.utils.*
import org.xapps.apps.todox.viewmodels.Constants
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject


class CategoryRepository @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val categoryDao: CategoryDao
) {

    suspend fun insert(categories: List<Category>): Either<CategoryFailure, List<Category>> = withContext(dispatcher) {
        info<CategoryRepository>("Insert $categories")
        try {
            val ids = categoryDao.insertAsync(categories)
            if(ids.size == categories.size) {
                categories.forEachIndexed { index, category -> category.id = ids[index] }
                info<CategoryRepository>("Categories successfully inserted")
                categories.toSuccess()
            } else {
                CategoryFailure.Database.toError()
            }
        } catch (ex: Exception) {
            error<CategoryRepository>(ex, "Exception captured")
            CategoryFailure.Exception(ex.localizedMessage).toError()
        }
    }

    fun categoriesPaginated(): Flow<PagingData<Category>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true,
                maxSize = 50
            )
        ) {
            categoryDao.categoriesWithDateCountPaginatedAsync(LocalDate.now().parseToString(Constants.DATE_PATTERN_DB))
        }.flow.flowOn(dispatcher)
    }

    fun categories(): Flow<List<Category>> {
        Timber.i("About to call categoriesAsync")
        return categoryDao.categoriesAsync()
            .flowOn(dispatcher)
    }

    fun category(id: Long): Flow<Category> {
        return categoryDao.categoryAsync(id)
            .flowOn(dispatcher)
    }

    suspend fun insertCategory(category: Category): Either<CategoryFailure, Category> = withContext(dispatcher) {
        try {
            val id = categoryDao.insertAsync(category)
            if(id != Constants.ID_INVALID) {
                category.id = id
                category.toSuccess()
            } else {
                CategoryFailure.Database.toError()
            }
        } catch (ex: Exception) {
            CategoryFailure.Exception(ex.localizedMessage).toError()
        }
    }

    suspend fun updateCategory(category: Category): Either<CategoryFailure, Category> = withContext(dispatcher) {
        try {
            val count = categoryDao.updateAsync(category)
            if(count == 1) {
                category.toSuccess()
            } else {
                CategoryFailure.Database.toError()
            }
        } catch (ex: Exception) {
            CategoryFailure.Exception(ex.localizedMessage).toError()
        }
    }

}