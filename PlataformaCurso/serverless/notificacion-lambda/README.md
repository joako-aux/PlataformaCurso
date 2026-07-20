# Función serverless de notificaciones

Implementa el requerimiento IL3.4/IL3.5 de la EP3: una arquitectura
serverless (FaaS) con dos funciones Lambda, una cola SQS y un API
Gateway, integrada con `notificacion-service`.

```
Cliente / Postman
      |
      v  POST /notificaciones (con x-api-key)
API Gateway (AWS::Serverless::Api)
      |
      v
enviarNotificacion (Lambda productora) --> SQS: plataformacurso-notificaciones
                                                  |
                                                  v
                                    procesarNotificacion (Lambda consumidora)
                                                  |
                                                  v (opcional)
                                      notificacion-service (microservicio)
```

## Requisitos

- AWS CLI configurado (`aws configure`)
- [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)
- Node.js 20+

## Desplegar

```bash
cd serverless/notificacion-lambda
sam build
sam deploy --guided \
  --stack-name plataformacurso-serverless \
  --capabilities CAPABILITY_IAM \
  --parameter-overrides NotificacionServiceUrl=http://<ip-o-dns-publico>:8086
```

Al finalizar, SAM entrega en los `Outputs` la URL del API Gateway y
la URL/ARN de la cola SQS.

## Probar

1. Obtener la API key generada:
   ```bash
   aws apigateway get-api-keys --name-query PlataformaCursoUsagePlan --include-values
   ```
2. Enviar una notificación de prueba (flujo productor -> cola -> consumidor, IE14/IE15):
   ```bash
   curl -X POST https://<api-id>.execute-api.<region>.amazonaws.com/prod/notificaciones \
     -H "x-api-key: <API_KEY>" \
     -H "Content-Type: application/json" \
     -d '{"usuarioId": 1, "curso": "JVY0101", "mensaje": "Tu inscripción fue confirmada"}'
   ```
3. Ver el procesamiento en CloudWatch Logs de `ProcesarNotificacionFunction`:
   ```bash
   sam logs -n ProcesarNotificacionFunction --stack-name plataformacurso-serverless --tail
   ```

## Eliminar recursos

```bash
sam delete --stack-name plataformacurso-serverless
```
