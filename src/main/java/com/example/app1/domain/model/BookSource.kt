package com.example.app1.domain.model

import android.net.Uri

sealed class BookSource {
    // Para archivos locales o subidos propios (Usa la Uri segura de Android SAF)
    data class Local(val uri: Uri) : BookSource()

    // Para archivos públicos de internet (URL de descarga o streaming)
    data class Remote(val url: String, val isStreaming: Boolean = false) : BookSource()
}