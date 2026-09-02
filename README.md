#  LUMINA — Gestión Bibliográfica para Android

LUMINA es una aplicación Android de gestión bibliográfica que combina la consulta de catálogos globales mediante la API de Google Books con la importación y organización local de la biblioteca personal del usuario.

![Android API 24+](https://img.shields.io/badge/Android-7.0%2B%20(API%2024)-green.svg)
![Kotlin 2.1.0](https://img.shields.io/badge/Kotlin-2.1.0-purple.svg)
![Architecture MVVM](https://img.shields.io/badge/Architecture-MVVM-blue.svg)
![UI Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg)

---

##  Arquitectura y Tecnologías

El proyecto implementa la arquitectura recomendada por Google (**MVVM + Repository Pattern**) con un flujo de datos unidireccional (UDF):

* **Lenguaje:** Kotlin
* **UI:** Jetpack Compose (Diseño 100% declarativo)
* **Persistencia Local:** Room Database (SQLite)
* **Red:** Retrofit 2 + Gson Converter
* **Asincronía & Flujos:** Kotlin Coroutines + StateFlow / Flow
* **Navegación:** Navigation Compose
* **Almacenamiento Local:** Storage Access Framework (SAF)
* **Carga de Imágenes:** Coil Compose

---

##  Funcionalidades Principales

* **Exploración Global (Google Books API):** Búsqueda remota de libros en tiempo real con optimización de peticiones (*Debouncing*).
* **Gestión de Biblioteca (Room):** Guardado y clasificación de obras en tres estados de lectura (*Por leer*, *Leyendo*, *Leídos*).
* **Importación de Documentos (PDF):** Integración con Storage Access Framework (SAF) para vincular archivos locales y asignarles metadatos personalizados (título, autor, descripción).
* **Filtros por Categoría:** Navegación por géneros literarios (Fantasía, Historia, Filosofía, Ciencia, etc.).
* **Detalle de Libro:** Visualización de sinopsis, autores, fecha de publicación y portadas.

---

##  Requisitos del Sistema

* **API Mínima:** Android 7.0 (API Level 24)
* **API Objetivo / Compile SDK:** Android 14 (API Level 34 / 35)
* **Entorno de Desarrollo:** Android Studio Ladybug / Iguana o superior
* **JDK:** Java 17 / 21

---

##  Instalación y Configuración

1. Clona este repositorio:
   ```bash
   git clone [https://github.com/tu-usuario/lumina.git](https://github.com/tu-usuario/lumina.git)
