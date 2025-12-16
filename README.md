# Huerto Hogar (App Móvil)

## Integrantes
- [Aurora Gonzalez]
- [Bastian Burgos]
- [Samantha Munizaga]
- 
## Descripción / Contexto
App móvil de e-commerce para venta de productos de tipo orgánicos .
Incluye autenticación, catálogo, carrito, checkout y módulo de administración.

## Roles de usuario
- ADMIN: [Puede ver todos los usuarios , la cantidad de productos y sus cantidades y controla las ordenes]
- USUARIO: [Puede navegar el home y generar contactos para notificar cosas , puede agregar productos al carrito y generar compras y por ultimo editar su perfil ]
> Nota: Actualmente el proyecto tiene 2 roles.

## Funcionalidades principales
- Registro e inicio de sesión (JWT)
- Recuperación de contraseña
- Modificación de perfil (incluye [foto / región / etc.])
- Catálogo y detalle de productos
- Carrito y checkout
- Administración (gestión de productos/usuarios/órdenes)
- Consumo API externa (OpenWeather) mostrado en [pantalla]

## API externa utilizada
- OpenWeather: clima por coordenadas (Retrofit)

## Endpoints utilizados (backend propio)
Base URL: `http://[IP]:8080/`

### Auth / Usuario
- POST `/api/usuario/login`
- POST `/api/usuario/guardar`
- POST `/api/usuario/recuperar-contrasenna`
- PUT  `/api/usuario/actualizar-contrasenna`
- PUT  `/api/usuario//{id}/foto-perfil`

### Productos
- GET `/api/producto`
- GET `/api/producto/buscar/{id}`
- POST `/api/producto/crear`
- PUT `/api/producto/actualizar/{id}`
- DELETE `/api/producto/eliminar/{id}`

### Órdenes
- POST `/api/orden/guardar`
- GET `/api/orden`
- PUT `/api/orden/actualizar-estado/{idOrden}`
- DELETE `/api/orden/eliminar/{idOrden}`

### Contacto
- POST `/api/contacto`
- GET `/api/contacto/buscar/{id}`
- POST `/api/contacto/crear`
- DELETE `/api/contacto/eliminar/{id}`


## Persistencia
- Local: Room (carrito / [lo que guardes])
- Externa: MySQL (microservicios/backend)

## Recursos nativos usados
- [Ej: Ubicación (GPS) para checkout]
- [Ej: Cámara/Galería para foto de perfil]

## Pruebas unitarias
Se incluyeron pruebas con JUnit + MockK en:
- `CheckoutInfoTest`
- `CheckoutViewModelTest`
- `WeatherRepositoryTest`

## Cómo ejecutar el proyecto

### Backend (Spring Boot)
1. Configurar base de datos MySQL: [nombre bd, usuario, pass]
2. Ejecutar:
   - `mvn spring-boot:run`
   o correr desde el IDE
3. Backend queda en: `http://localhost:8080/`

### App móvil (Android)
1. Abrir proyecto en Android Studio
2. Cambiar baseURL a la IP del PC si se prueba en celular (ej: `http://192.168.X.X:8080/`)
3. Ejecutar en emulador o dispositivo físico

## APK firmado + Keystore (.jks)
- APK firmado: `./apk/app-release.apk`
- Keystore (.jks): `./app/keystore/huerto_release.jks`
- Configuración de firmado: `app/build.gradle.kts` (signingConfigs -> release)

## Planificación (Trello)
- Tablero: [pega aquí el link público o invitación]
## Evidencia de trabajo colaborativo
- Commits y aportes por integrante: (GitHub -> Insights -> Contributors)
