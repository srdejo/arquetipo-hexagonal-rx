# Arquetipo Hexagonal Reactivo — Spring WebFlux

Plantilla base para construir microservicios **reactivos** en **Java + Spring Boot (WebFlux + R2DBC)** siguiendo la **arquitectura hexagonal** (puertos y adaptadores). Es la versión reactiva de [arquetipo-hexagonal](https://github.com/srdejo/arquetipo-hexagonal): misma estructura de capas, pero de extremo a extremo con `Mono`/`Flux` y sin bloquear hilos. Incluye un CRUD mínimo de ejemplo (`Object`) que recorre todas las capas, para que lo copies, lo renombres y empieces a construir tu propio dominio.

### Stack

* ![Java](https://img.shields.io/badge/java-26-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
* ![Spring](https://img.shields.io/badge/Spring_Boot-4.1-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
* ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
* ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)

Además: Spring WebFlux, Spring Data R2DBC (driver `r2dbc-postgresql`), Flyway para migraciones, Bean Validation, MapStruct, Lombok, springdoc-openapi para WebFlux (Swagger UI), Reactor Test (`StepVerifier`), H2 R2DBC para tests y JaCoCo para cobertura.

---

## ¿Qué es la arquitectura hexagonal?

La arquitectura hexagonal (Alistair Cockburn, también llamada *Ports & Adapters*) busca que **la lógica de negocio no dependa de ninguna tecnología**. El dominio queda en el centro y todo lo demás —HTTP, base de datos, colas, APIs externas— se conecta a él desde afuera.

```
                ┌─────────────────────────────────────────────┐
   HTTP  ──►    │  Adaptador de entrada (RestController)      │
                │              │  Mono / Flux                 │
                │              ▼                              │
                │  Aplicación (Handler, DTOs, Mappers)        │
                │              │                              │
                │              ▼                              │
                │   ┌──────────────────────────────────┐      │
                │   │ DOMINIO                          │      │
                │   │  api/  ◄── puerto de entrada     │      │
                │   │  usecase/ (reglas de negocio)    │      │
                │   │  spi/  ──► puerto de salida      │      │
                │   └──────────────────────────────────┘      │
                │              │                              │
                │              ▼                              │
                │  Adaptador de salida (R2dbcAdapter) ──► DB  │
                └─────────────────────────────────────────────┘
```

| Concepto | Qué es | En este proyecto |
|---|---|---|
| **Dominio** | Modelos y reglas de negocio. Java puro + tipos de Reactor, sin Spring ni R2DBC. | `domain/model`, `domain/usecase` |
| **Puerto de entrada** (*driving port*) | Interfaz que expone lo que el dominio sabe hacer. | `domain/api/IObjectServicePort` |
| **Puerto de salida** (*driven port*) | Interfaz que declara lo que el dominio *necesita* del exterior. | `domain/spi/IObjectPersistencePort` |
| **Adaptador de entrada** | Traduce una tecnología de entrada (REST/WebFlux) a llamadas al dominio. | `infrastructure/input/rest` |
| **Adaptador de salida** | Implementa un puerto de salida con una tecnología concreta (R2DBC). | `infrastructure/out/r2dbc/adapter` |

**La regla de oro: las dependencias apuntan hacia adentro.** `infrastructure` conoce a `application` y a `domain`; `domain` no conoce a nadie (la única dependencia permitida es `reactor-core`, para poder expresar `Mono`/`Flux` en los puertos).

---

## Estructura del proyecto

```
src/main/java/co/com/srdejo
├── domain                      # Núcleo: sin dependencias de frameworks
│   ├── api/                    # Puertos de entrada (IObjectServicePort)
│   ├── spi/                    # Puertos de salida (IObjectPersistencePort)
│   ├── usecase/                # Implementación de los puertos de entrada (ObjectUseCase)
│   ├── model/                  # Modelos de dominio (ObjectModel)
│   └── exception/              # Excepciones de negocio
│
├── application                 # Orquestación entre el mundo exterior y el dominio
│   ├── handler/                # Handlers: reciben DTOs, llaman al puerto de entrada
│   ├── dto/request|response/   # Contratos de la API
│   └── mapper/                 # DTO ⇄ modelo de dominio (MapStruct)
│
└── infrastructure              # Detalles técnicos
    ├── input/rest/             # Adaptador de entrada: controladores WebFlux
    ├── out/r2dbc/              # Adaptador de salida: entidades, repositorios reactivos, mappers, adapter
    ├── configuration/          # BeanConfiguration: conecta puertos con adaptadores
    ├── exceptionhandler/       # @RestControllerAdvice
    ├── exception/              # Excepciones técnicas
    └── documentation/          # Configuración de OpenAPI
```

### Flujo de una petición

`POST /api/v1/object/`

1. `ObjectRestController` recibe el `ObjectRequestDto` (validado con `@Valid`) y devuelve un `Mono<ResponseEntity<Void>>`.
2. `ObjectHandler` lo convierte a `ObjectModel` con `IObjectRequestMapper` y llama a `IObjectServicePort`.
3. `ObjectUseCase` aplica las reglas de negocio y llama a `IObjectPersistencePort`.
4. `ObjectR2dbcAdapter` convierte el modelo a `ObjectEntity` y lo guarda con `IObjectRepository` (`ReactiveCrudRepository`).

Nada se ejecuta hasta que WebFlux se suscribe a la cadena: cada capa **compone** publishers, no los resuelve.

### Diferencias frente al arquetipo imperativo

| Imperativo | Reactivo |
|---|---|
| `spring-boot-starter-web` | `spring-boot-starter-webflux` |
| Spring Data JPA + JDBC (`postgresql`) | Spring Data R2DBC + `r2dbc-postgresql` |
| `@Entity`, `@GeneratedValue` (Jakarta Persistence) | `@Table`, `@Id`, `@Column` de Spring Data Relational |
| Flyway sobre el `DataSource` JDBC de la app | Flyway con su propia conexión JDBC (`spring.flyway.url`), porque no se ejecuta sobre R2DBC |
| `T` / `List<T>` | `Mono<T>` / `Flux<T>` |
| `throw new NoDataFoundException()` | `switchIfEmpty(Mono.error(...))` |
| `MethodArgumentNotValidException` | `WebExchangeBindException` |
| `springdoc-openapi-starter-webmvc-ui` | `springdoc-openapi-starter-webflux-ui` |
| Tests con aserciones directas | Tests con `StepVerifier` |

---

## Cómo empezar

### Prerrequisitos

* JDK 26 (Gradle toolchain lo puede descargar automáticamente)
* Una base de datos PostgreSQL accesible
* Gradle — opcional, el proyecto incluye el wrapper `./gradlew`

### Instalación

1. Clona el repositorio

   ```sh
   git clone git@github.com:srdejo/arquetipo-hexagonal-rx.git
   cd arquetipo-hexagonal-rx
   ```

2. Define las variables de entorno de conexión (todas tienen un valor por defecto en `application.yml`):

   | Variable | Descripción | Por defecto |
   |---|---|---|
   | `DB_HOST` | Host de PostgreSQL | `localhost` |
   | `DB_PORT` | Puerto | `5432` |
   | `DB_NAME` | Nombre de la base de datos | `powerup_db` |
   | `DB_USERNAME` | Usuario | `postgres` |
   | `DB_PASSWORD` | Contraseña | `postgres` |

   Al arrancar, **Flyway** aplica las migraciones de `src/main/resources/db/migration`. Flyway trabaja sobre JDBC, por eso `application.yml` define además `spring.flyway.url` con el driver `postgresql`; la aplicación sigue usando solo R2DBC.

### Ejecutar

```sh
./gradlew bootRun
```

La aplicación arranca en el puerto **8081**. Abre la documentación interactiva en
[http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html).

Prueba rápida:

```sh
curl -X POST http://localhost:8081/api/v1/object/ \
  -H "Content-Type: application/json" -d '{"name":"mi primer objeto"}'

curl http://localhost:8081/api/v1/object/
```

### Tests

```sh
./gradlew test
```

Los tests usan H2 en memoria (modo PostgreSQL): Flyway aplica las mismas migraciones por JDBC y la app las consulta vía R2DBC (`src/test/resources/application.yml`). Los casos de uso se prueban con mocks de los puertos y `StepVerifier`. El reporte de cobertura de JaCoCo queda en `build/reports/jacoco/test/html/index.html`.

---

## Cómo usar el arquetipo para tu propio dominio

Supongamos que quieres modelar `Restaurant`:

1. **Renombra el paquete base** `co.com.srdejo` (y `group` en `build.gradle`) al de tu proyecto.
2. **Dominio** (empieza siempre por aquí):
   - `domain/model/Restaurant` — el modelo con sus reglas.
   - `domain/api/IRestaurantServicePort` — qué operaciones ofrece, devolviendo `Mono`/`Flux`.
   - `domain/spi/IRestaurantPersistencePort` — qué necesita persistir.
   - `domain/usecase/RestaurantUseCase` — implementa el puerto de entrada, recibe el de salida por constructor. Sin `@Service`, sin `@Autowired`.
3. **Infraestructura de salida**: `RestaurantEntity`, `IRestaurantRepository extends ReactiveCrudRepository`, `IRestaurantEntityMapper` y `RestaurantR2dbcAdapter implements IRestaurantPersistencePort`. Crea la tabla con una nueva migración `db/migration/V2__create_restaurant_table.sql` (nunca edites una migración ya aplicada).
4. **Aplicación**: DTOs de request/response, sus mappers y `RestaurantHandler`.
5. **Infraestructura de entrada**: `RestaurantRestController`, que solo habla con el handler.
6. **Cablea** los nuevos puertos y adaptadores en `BeanConfiguration`.
7. Elimina el ejemplo `Object*` cuando ya no lo necesites.

### Reglas para no romper la arquitectura

- `domain` **no importa** nada de `application`, `infrastructure`, Spring, R2DBC ni Jackson (solo `reactor-core`).
- Los controladores **nunca** usan repositorios ni entidades directamente.
- Las entidades (`*Entity`) no salen de `infrastructure/out`; el resto del sistema trabaja con modelos de dominio.
- Las validaciones de negocio viven en el caso de uso y emiten errores con `Mono.error(...)` usando excepciones de `domain/exception`; `ControllerAdvisor` las traduce a respuestas HTTP.
- **Nunca bloquees**: nada de `.block()`, `Thread.sleep` ni librerías JDBC/bloqueantes dentro del flujo. Si no hay alternativa, aíslalo con `subscribeOn(Schedulers.boundedElastic())`.
- Si necesitas hablar con algo nuevo (otra API vía `WebClient`, una cola, un bucket), primero define el puerto en `domain/spi` y después el adaptador en `infrastructure/out`.
