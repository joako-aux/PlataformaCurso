# PlataformaCurso

Sistema de microservicios en Java/Spring Boot para la gestión de una
plataforma de cursos (usuarios, autenticación, cursos, inscripciones,
historial y notificaciones), con descubrimiento de servicios (Eureka),
API Gateway, despliegue en Docker Swarm, pipeline CI/CD y una
arquitectura serverless (cola SQS + Lambda) para notificaciones
asíncronas.

Desarrollado para la Evaluación Parcial N°3 de JVY0101 - Integración
y Despliegue de Soluciones Cloud.

## Arquitectura

```
                    ┌───────────────────────┐
   Cliente ───────► │      apigateway       │ (Spring Cloud Gateway + JWT)
                    └──────────┬────────────┘
                               │ lb:// (Eureka)
        ┌──────────┬──────────┼──────────┬──────────┬─────────────┐
        ▼          ▼          ▼          ▼          ▼             ▼
  auth-service usuario-  curso-   inscripcion- historial-  notificacion-
               service   service   service      service       service
        │          │          │          │          │             │
        └──────────┴──────────┴──────────┴──────────┴─────────────┘
                               │
                          mysql-db (MySQL 8)

  register-service (Eureka) — descubrimiento de todos los servicios

  notificacion-service ◄──── serverless/notificacion-lambda
                              (API Gateway AWS -> SQS -> Lambda)
```

## Microservicios

| Servicio               | Puerto | Descripción                          |
|-------------------------|--------|---------------------------------------|
| register-service        | 8761   | Servidor Eureka (service discovery)   |
| apigateway               | 8080   | Spring Cloud Gateway + validación JWT |
| auth-service             | 8081   | Login / registro / emisión de tokens  |
| usuario-service          | 8082   | Gestión de usuarios                   |
| curso-service             | 8083   | Gestión de cursos                     |
| inscripciones-service    | 8084   | Inscripciones a cursos                |
| historial-service        | 8085   | Historial académico                   |
| notificacion-service     | 8086   | Notificaciones (integra con la cola)  |

## Ejecutar en local (Docker Compose)

```bash
cd PlataformaCurso
docker compose up --build
```

Verificar salud de un servicio: `http://localhost:8080/actuator/health`
Endpoints vía Gateway, ej: `http://localhost:8080/api/auth/login`

## Docker Swarm

Ver `docker-stack.yml` y los scripts en `scripts/`.

1. En el nodo manager:
   ```bash
   ./scripts/swarm-init-manager.sh
   ```
2. En cada nodo worker (con el token/IP que entregó el paso anterior):
   ```bash
   ./scripts/swarm-join-worker.sh <TOKEN> <IP_MANAGER>:2377
   ```
3. Verificar el clúster desde el manager:
   ```bash
   docker node ls
   ```
4. Publicar el stack (requiere imágenes ya publicadas por el pipeline CI/CD):
   ```bash
   export IMAGE_PREFIX=ghcr.io/<tu-usuario>/plataformacurso
   export IMAGE_TAG=latest
   ./scripts/swarm-deploy.sh
   ```
5. Escalar un servicio:
   ```bash
   ./scripts/swarm-scale.sh apigateway 4
   ./scripts/swarm-scale.sh apigateway 1
   ```

### Decisiones técnicas del clúster

- **Réplicas por defecto = 2** en los microservicios de negocio: tolera
  la caída de un nodo/contenedor sin interrumpir el servicio
  (disponibilidad).
- **`mysql-db` restringido a `node.role == manager`**: usa un volumen
  local (`mysql_data`); fijarlo a un nodo evita que Swarm lo reprograme
  en un worker sin los datos (evita pérdida de datos). En un entorno
  productivo real se reemplazaría por un volumen en red o una base de
  datos gestionada (ej. RDS).
- **`update_config: order: start-first`**: al actualizar un servicio,
  primero levanta el contenedor nuevo y luego apaga el viejo → sin
  downtime.
- **`restart_policy: on-failure`**: reinicia automáticamente los
  contenedores que fallan, sin generar reinicios en bucle en errores
  de configuración permanentes.
- **Healthchecks basados en `/actuator/health`**: permiten a Swarm y a
  Compose saber cuándo un contenedor está realmente listo antes de
  enrutar tráfico o levantar servicios dependientes.

## CI/CD

Ver `.github/workflows/ci-cd.yml`. Etapas:

1. **test**: `mvn test` de cada microservicio (matriz).
2. **build-and-push**: build de la imagen Docker de cada servicio y
   push a GitHub Container Registry (GHCR).
3. **provision-serverless**: `sam build && sam deploy` — aprovisiona
   (infraestructura como código) la cola SQS, el API Gateway y las
   Lambdas de `serverless/notificacion-lambda`.
4. **deploy-swarm**: se conecta por SSH al nodo manager y publica
   `docker-stack.yml` con las imágenes recién construidas.

### Secrets requeridos en GitHub

| Secret                     | Uso                                      |
|-----------------------------|-------------------------------------------|
| `AWS_ACCESS_KEY_ID`         | Credenciales AWS para SAM y despliegue    |
| `AWS_SECRET_ACCESS_KEY`     | Credenciales AWS                          |
| `AWS_REGION`                 | Región AWS (ej. `us-east-1`)              |
| `AWS_HOST`                   | IP pública del nodo manager de Swarm      |
| `AWS_USER`                   | Usuario SSH del nodo manager              |
| `AWS_SSH_KEY`                 | Llave privada SSH                         |
| `SQS_QUEUE_URL`               | URL de la cola (salida de `sam deploy`)   |
| `NOTIFICACION_SERVICE_URL`   | URL pública de `notificacion-service`      |

## Arquitectura serverless (cola + FaaS + API Gateway)

Ver `serverless/notificacion-lambda/README.md` para el detalle
completo, despliegue y pruebas.

Resumen: un cliente llama a un endpoint HTTP protegido por **API
Gateway** (requiere API key) → una función Lambda **productora**
encola el mensaje en **SQS** → una función Lambda **consumidora** es
disparada automáticamente por la cola, procesa el mensaje y
opcionalmente lo reenvía a `notificacion-service`.

## Prueba de extremo a extremo

1. `docker stack deploy` (o `docker compose up`) para levantar los
   microservicios.
2. Login vía Gateway: `POST /api/auth/login` → obtener JWT.
3. Crear una inscripción: `POST /api/inscripciones` (con `Authorization: Bearer <token>`).
4. Enviar una notificación vía API Gateway serverless (ver sección
   anterior) → verificar en CloudWatch Logs que la Lambda consumidora
   la procesó y (si está configurado) que llegó a `notificacion-service`.
5. Escalar `apigateway` con `swarm-scale.sh` mientras se repiten las
   peticiones, para demostrar que el sistema sigue respondiendo.

## Estructura del repositorio

```
PlataformaCurso/
├── apigateway/
├── auth-service/
├── usuario-service/
├── curso-service/
├── inscripciones-service/
├── historial-service/
├── notificacion-service/
├── register-service/
├── serverless/
│   └── notificacion-lambda/   # Lambdas + template SAM (cola, API Gateway)
├── scripts/                   # Scripts de administración de Swarm
├── docker-compose.yml         # Entorno local
├── docker-stack.yml           # Stack para Docker Swarm
└── .github/workflows/ci-cd.yml
```
