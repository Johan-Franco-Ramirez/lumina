# Lumina 📚

**Lumina** es una moderna aplicación de lectura y gestión de biblioteca personal construida para Android. Diseñada con una interfaz elegante y minimalista, permite a los lectores explorar un vasto catálogo de obras, gestionar sus lecturas actuales y digitalizar su propia biblioteca física.

---

## ✨ Características Principales

- **🔍 Exploración Global**: Integración dual con **Open Library API** y **Gutendex**, permitiendo buscar desde los últimos best-sellers hasta clásicos de dominio público de forma totalmente abierta y gratuita.
- **📜 Scroll Infinito**: Implementación de **Paging 3** para una navegación fluida por miles de resultados de búsqueda sin afectar el rendimiento del dispositivo.
- **📖 Lectura Integrada**: Acceso directo a previsualizaciones de libros y lectura online de textos clásicos sin salir de la app.
- **📁 Biblioteca Personal (PDF)**: Importa tus propios archivos PDF para tener todos tus documentos y libros personales organizados en un solo lugar.
- **⏳ Gestión de Estados**: Organiza tu flujo de lectura en tres categorías inteligentes:
    - **Por leer**: Tu lista de deseos y pendientes.
    - **Leyendo**: Acceso rápido a tus obras actuales.
    - **Leídos**: Tu historial de conquistas literarias.
- **🏠 Inicio Dinámico**: Una pantalla principal que se adapta a ti, mostrando secciones de "Continúa leyendo", libros recomendados aleatorios de las APIs y múltiples categorías (Misterio, Aventura, Sci-Fi).
- **🗑️ Control Total**: Elimina fácilmente cualquier libro de tu biblioteca para mantener tu colección limpia y actualizada.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: [Kotlin](https://kotlinlang.org/)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Arquitectura**: MVVM (Model-View-ViewModel) + Capa de Repositorio.
- **Paginación**: [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data) para carga eficiente de grandes volúmenes de datos.
- **Base de Datos**: [Room](https://developer.android.com/training/data-storage/room) para persistencia local.
- **Red**: [Retrofit](https://square.github.io/retrofit/) & [Gson](https://github.com/google/gson) para consumo de las APIs de Open Library y Gutendex.
- **Carga de Imágenes**: [Coil](https://coil-kt.github.io/coil/) para carga asíncrona de portadas.
- **Control de Versiones**: Gradle Version Catalog (`libs.versions.toml`).

---

## 📖 Funcionamiento Técnico del Lector de PDFs

El módulo del lector de PDF está diseñado siguiendo una arquitectura reactiva dividida en tres capas principales:

### 1. Capa de Datos (`ReaderRepositoryImpl.kt`)
Se utiliza la API nativa del sistema operativo Android (`android.graphics.pdf.PdfRenderer`) para acceder de manera directa al almacenamiento interno o externo:
- A través de `ParcelFileDescriptor.open()` se abre el archivo PDF local en modo lectura.
- Con `PdfRenderer`, cada página del documento se decodifica y dibuja individualmente sobre un `Bitmap` de alta resolución en modo `RENDER_MODE_FOR_DISPLAY`.
- Se implementa gestión explícita de memoria liberando recursos (`page.close()` y `fileDescriptor.close()`) para prevenir fugas de memoria o fallos `OutOfMemoryError`.

### 2. Capa de Lógica & Estado (`ReaderViewModel.kt`)
- La conversión e interpretación pesada del archivo PDF se ejecuta en hilos secundarios desacoplados del hilo principal mediante corrutinas de Kotlin en `viewModelScope` con `Dispatchers.IO`.
- Mantiene y emite el estado del lector mediante un `StateFlow<ReaderUiState>` reactivo, gestionando la página actual, el total de páginas y la persistencia automática del progreso de lectura.

### 3. Capa de Interfaz (`ReaderScreen.kt`)
- Construida íntegramente con **Jetpack Compose**.
- Transforma los objetos `Bitmap` procesados a un `ImageBitmap` dibujable nativamente por Compose mediante `.asImageBitmap()`.
- Incorpora soporte para gestos táctiles avanzadas (pizcar para zoom, pan/desplazamiento) utilizando transformadores de puntero (`pointerInput` y `detectTransformGestures`).

## 🏗️ Estructura del Proyecto

```text
com.example.app1
├── data           # Servicios API, DAO de Room, Repositorios y Paging Sources
├── domain         # Modelos de datos puros (Clean Architecture)
├── ui
│   ├── components # Componentes reutilizables (Cards, Badges, etc.)
│   ├── screens    # Pantallas principales (Home, Search, Library, Detail)
│   └── theme      # Definición de colores, tipos y temas
└── viewmodel      # Lógica de negocio y gestión de estado de la UI
```

---

## 🚀 Instalación y Configuración

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/Johan-Franco-Ramirez/lumina.git
   ```
2. **Abrir en Android Studio**:
   - Se recomienda la versión Ladybug o superior.
   - Esperar a que termine la sincronización de Gradle.
3. **Configuración de Firebase**:
   - Asegúrate de incluir tu propio archivo `google-services.json` en la carpeta `app/` para habilitar las funcionalidades de Firebase.
4. **Ejecutar**:
   - Selecciona un dispositivo físico o emulador con API 24 (Android 7.0) o superior.

---

## 📸 Pantallas

- **Inicio**: Descubre nuevas obras dinámicas y retoma tus lecturas.
- **Explorar**: Buscador inteligente con scroll infinito y filtros avanzados.
- **Mi Biblioteca**: Organiza tus PDFs y libros favoritos.
- **Detalle**: Sinopsis completa, autor y opciones de lectura directa.

---
*Desarrollado con ❤️ por Lumina Group.*

- Johan Franco Ramirez 
- Jhon Harold Sanchez
- Yeiker Daniel Solano
