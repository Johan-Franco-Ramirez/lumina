package com.example.app1.domain.model

import android.net.Uri

sealed class BookSource {
    // Para un solo archivo local
    data class Local(val uri: Uri) : BookSource()

    // Para una colección de archivos (Carpetas o Series)
    data class Collection(val uris: List<Uri>) : BookSource()

    // Para archivos públicos de internet
    data class Remote(val url: String, val isStreaming: Boolean = false) : BookSource()
}
