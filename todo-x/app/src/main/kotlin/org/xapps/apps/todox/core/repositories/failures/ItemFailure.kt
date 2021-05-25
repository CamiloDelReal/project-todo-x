package org.xapps.apps.todox.core.repositories.failures


sealed class ItemFailure {

    data class Exception(val description: String?): ItemFailure()

    object Database: ItemFailure()

}