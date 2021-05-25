package org.xapps.apps.todox.core.repositories.failures


sealed class TaskFailure {

    data class Exception(val description: String?): TaskFailure()

    object Database: TaskFailure()

}