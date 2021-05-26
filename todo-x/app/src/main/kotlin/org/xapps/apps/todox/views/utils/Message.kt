package org.xapps.apps.todox.views.utils

import java.lang.Exception


sealed class Message {

    data class Success(val data: Any? = null): Message()
    data class Error(val exception: Exception): Message()
    object Loading: Message()
    object Loaded: Message()

}