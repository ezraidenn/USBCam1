# 📱 USB CAM Pro - Professional USB Camera App

**Aplicación Android profesional para control de cámaras USB con diseño premium empresarial**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## ✨ Características Principales

### 📷 Control de Cámara
- ✅ Detección automática de cámaras USB/UVC
- ✅ Preview en tiempo real con Surface rendering
- ✅ Selector de resoluciones y FPS (hasta 4K60)
- ✅ Soporte para múltiples cámaras USB
- ✅ Rotación y espejo de imagen
- ✅ Modo pantalla completa

### 🎬 Captura y Grabación
- ✅ Captura de fotos en alta calidad (JPEG)
- ✅ Grabación de video (próximamente)
- ✅ Guardado automático en galería
- ✅ Galería interna de capturas
- ✅ Visualización y eliminación de medios

### 🎨 Herramientas Profesionales
- ✅ Grid overlay (regla de tercios, cuadrícula, proporción áurea)
- ✅ Indicadores de estado en tiempo real
- ✅ Indicador de FPS
- ✅ Indicador de grabación
- 🔄 Controles de imagen (próximamente)
- 🔄 Histograma y vectorscopio (próximamente)

### 🎯 Diseño Premium
- ✅ UI moderna estilo Apple/empresarial
- ✅ Material Design 3
- ✅ Animaciones fluidas
- ✅ Tema claro profesional
- ✅ Iconografía consistente
- ✅ Experiencia de usuario optimizada

## 🏗️ Arquitectura

### Stack Tecnológico
- **Lenguaje:** Kotlin 100%
- **UI:** Jetpack Compose + Material3
- **Arquitectura:** MVVM + Clean Architecture
- **DI:** Hilt (Dagger)
- **Async:** Kotlin Coroutines + Flow
- **Cámara USB:** UVCAndroid
- **Imágenes:** Coil
- **Logging:** Timber
- **Media:** Media3 (ExoPlayer)

### Estructura del Proyecto
```
app/src/main/java/com/biostar/camtest/
├── di/                          # Dependency Injection
│   └── AppModule.kt
├── data/
│   ├── models/                  # Data models
│   │   └── CameraModels.kt
│   └── repository/              # Repositories
│       ├── CameraRepository.kt
│       └── MediaRepository.kt
├── ui/
│   ├── theme/                   # Material3 Theme
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Theme.kt
│   ├── components/              # Reusable UI components
│   │   ├── Buttons.kt
│   │   └── StatusIndicators.kt
│   ├── navigation/              # Navigation
│   │   └── Navigation.kt
│   └── screens/
│       ├── camera/              # Camera screen
│       │   ├── CameraScreen.kt
│       │   ├── CameraViewModel.kt
│       │   └── ResolutionSelectorSheet.kt
│       └── gallery/             # Gallery screen
│           ├── GalleryScreen.kt
│           └── GalleryViewModel.kt
├── MainActivity.kt
└── USBCamApp.kt                 # Application class
```

## 📋 Requisitos

- **Android:** 8.0+ (API 26+)
- **Hardware:** Soporte USB OTG
- **Cámara:** Compatible con UVC (USB Video Class)
- **Espacio:** ~15 MB

## 🚀 Instalación

### Opción 1: Android Studio (Recomendado)

1. **Clonar el repositorio:**
```bash
git clone https://github.com/ezraidenn/USBCam1.git
cd USBCam1
```

2. **Abrir en Android Studio:**
   - File → Open
   - Selecciona la carpeta del proyecto
   - Espera a que Gradle sincronice

3. **Compilar la APK:**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - La APK estará en: `app/build/outputs/apk/debug/`

### Opción 2: Línea de Comandos

```bash
# Compilar APK de debug
./gradlew assembleDebug

# Compilar APK de release
./gradlew assembleRelease

# Instalar en dispositivo conectado
./gradlew installDebug
```

## 📲 Uso

1. **Conecta tu cámara USB** al dispositivo Android mediante cable OTG
2. **Abre la app** "USB CAM Pro"
3. **Acepta los permisos** de cámara y almacenamiento
4. **Presiona "Iniciar Cámara"** para comenzar
5. **Selecciona la resolución** deseada desde el selector
6. **Captura fotos** con el botón blanco circular
7. **Graba videos** con el botón rojo (próximamente)
8. **Accede a la galería** para ver tus capturas

## 🎛️ Funcionalidades Detalladas

### Selector de Resoluciones
- Detecta automáticamente todas las resoluciones soportadas por tu cámara
- Muestra opciones en formato legible (4K, 1080p, 720p, etc.)
- Permite cambiar resolución en tiempo real
- Indica la resolución actual en la barra superior

### Grid Overlay (Regla de 3)
- **Regla de tercios:** Grid 3x3 para composición fotográfica
- **Cuadrícula:** Grid 4x4 para alineación precisa
- **Proporción áurea:** Grid basado en la proporción áurea (1.618)
- Toggle on/off con botón dedicado

### Galería Interna
- Muestra solo las capturas realizadas con la app
- Thumbnails optimizados con Coil
- Indicadores de tipo de medio (foto/video)
- Eliminación con confirmación
- Ordenadas por fecha (más recientes primero)

## 🔧 Configuración Avanzada

### Cambiar Calidad de Captura
Edita `MediaRepository.kt` línea 42:
```kotlin
bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream) // 95 = calidad (0-100)
```

### Personalizar Colores
Edita `ui/theme/Color.kt`:
```kotlin
val AccentBlue = Color(0xFF007AFF) // Color principal
val RecordingRed = Color(0xFFFF3B30) // Color de grabación
```

### Agregar Nuevos Grid Types
Edita `data/models/CameraModels.kt`:
```kotlin
enum class GridType {
    RULE_OF_THIRDS,
    SQUARE,
    GOLDEN_RATIO,
    YOUR_NEW_TYPE  // Agregar aquí
}
```

## 🐛 Solución de Problemas

### Cámara no detectada
- Verifica que tu dispositivo soporte USB OTG
- Asegúrate de que la cámara sea compatible con UVC
- Intenta con otro cable OTG
- Reinicia el dispositivo

### Permisos denegados
- Ve a: Configuración → Apps → USB CAM Pro → Permisos
- Activa: Cámara, Almacenamiento

### App no compila
- Verifica que tengas Android Studio Hedgehog o superior
- Asegúrate de tener JDK 17 instalado
- Ejecuta: `./gradlew clean build`

### Error de Hilt
- Rebuild project: Build → Rebuild Project
- Invalida caché: File → Invalidate Caches / Restart

## 📝 Roadmap

### v2.1 (Próximo)
- [ ] Grabación de video con MediaCodec
- [ ] Controles de imagen (brillo, contraste, saturación)
- [ ] Zoom digital
- [ ] Enfoque manual

### v2.2
- [ ] Streaming RTMP
- [ ] Timelapse
- [ ] Modo burst (ráfaga)
- [ ] Filtros en tiempo real

### v2.3
- [ ] Múltiples cámaras simultáneas
- [ ] Picture-in-Picture
- [ ] Chroma key (fondo verde)
- [ ] Exportación de video

## 🤝 Contribuir

Las contribuciones son bienvenidas! Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver `LICENSE` para más detalles.

## 👥 Autores

- **BioStar Team** - *Desarrollo inicial* - [GitHub](https://github.com/ezraidenn)

## 🙏 Agradecimientos

- [UVCAndroid](https://github.com/herohan/UVCAndroid) - Librería para cámaras USB
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - UI moderna
- [Material Design 3](https://m3.material.io/) - Sistema de diseño

## 📞 Soporte

Para issues y solicitudes de features, por favor crea un issue en GitHub.

---

**Hecho con ❤️ usando Kotlin y Jetpack Compose**
