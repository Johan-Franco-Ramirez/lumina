# Fix Gradle Version Incompatibility

The error `java.lang.NoClassDefFoundError: org/gradle/features/binding/ProjectTypeBinding` occurs because the **Android Gradle Plugin (AGP) 9.4.0** and **Kotlin 2.4.10** configured in the project require a newer version of Gradle (9.6.0+) than what is currently running. Additionally, the project is missing the standard Gradle Wrapper files (`gradlew`, `gradle-wrapper.properties`), leading to unpredictable build behavior.

## User Review Required

> [!IMPORTANT]
> I will downgrade the versions of the Android Gradle Plugin and Kotlin to the latest stable and widely compatible versions. The current versions (AGP 9.4.0, Kotlin 2.4.10) appear to be experimental or from a future release cycle that is not compatible with your current Gradle environment.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Acer/OneDrive/Escritorio/estudio/Desarrollo_Android/Luminapp/gradle/libs.versions.toml)
- Downgrade `agp` from `9.4.0` to `8.7.3` (current stable).
- Downgrade `kotlin` from `2.4.10` to `2.0.21` (stable).
- Downgrade `composeBom` to a stable version (e.g., `2024.10.01`).
- Adjust `ksp` and other dependencies to match the Kotlin version.

#### [NEW] [gradle-wrapper.properties](file:///C:/Users/Acer/OneDrive/Escritorio/estudio/Desarrollo_Android/Luminapp/gradle/wrapper/gradle-wrapper.properties)
- Create a standard wrapper configuration using Gradle 8.10.2 (compatible with AGP 8.7.3).

## Verification Plan

### Automated Tests
- Run `gradle_sync` to ensure the project structure is recognized.
- Run `gradle_build` (task `:app:assembleDebug`) to verify the build completes without `NoClassDefFoundError`.

### Manual Verification
- Verify that the "Unable to load class" error message disappears from the IDE.
