# Reglas específicas del proyecto Lumina
# Estas reglas protegen el código de ser borrado o renombrado por error durante la compilación final.

# --- Modelos de Datos (DTOs) ---
# Es CRÍTICO mantener los nombres de los campos para que GSON y Retrofit funcionen.
-keepclassmembers class com.example.app1.data.model.** { *; }
-keepclassmembers class com.example.app1.domain.model.** { *; }

# --- Retrofit & OkHttp ---
-keepattributes Signature, Exceptions, InnerClasses
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit

# --- Room ---
-keep class androidx.room.RoomDatabase { *; }
-dontwarn androidx.room.**

# --- Firebase ---
-dontwarn com.google.firebase.**
