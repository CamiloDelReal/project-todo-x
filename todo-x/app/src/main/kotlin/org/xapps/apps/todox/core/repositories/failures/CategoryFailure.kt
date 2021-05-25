package org.xapps.apps.todox.core.repositories.failures


sealed class CategoryFailure {

    data class Exception(val description: String?): CategoryFailure()

    object Database: CategoryFailure()

}