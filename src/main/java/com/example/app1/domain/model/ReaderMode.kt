package com.example.app1.domain.model

sealed class ReaderMode {
    object Webtoon : ReaderMode()
    object PDF : ReaderMode()
    object MangaRTL : ReaderMode()   // Derecha a Izquierda (Manga)
    object ComicLTR : ReaderMode()   // Izquierda a Derecha (Cómic)
}