// Función serverless CONSUMIDORA.
// Disparada automáticamente por mensajes en la cola SQS.
// Procesa la notificación y, si NOTIFICACION_SERVICE_URL está
// definido, la reenvía al microservicio notificacion-service
// del sistema (integración FaaS <-> microservicios, IE13).

exports.handler = async (event) => {
  const serviceUrl = process.env.NOTIFICACION_SERVICE_URL;

  for (const record of event.Records) {
    const data = JSON.parse(record.body);
    console.log("Procesando notificación:", data);

    if (serviceUrl) {
      try {
        const resp = await fetch(`${serviceUrl}/api/notificaciones`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(data),
        });
        console.log(
          `Notificación reenviada a notificacion-service, status ${resp.status}`
        );
      } catch (err) {
        // No relanzamos el error para no reintentar infinitamente;
        // el mensaje irá a la DLQ tras maxReceiveCount si sigue fallando.
        console.error("No se pudo contactar notificacion-service:", err.message);
      }
    } else {
      console.log(
        "NOTIFICACION_SERVICE_URL no configurado, solo se registra el evento."
      );
    }
  }

  return { batchItemFailures: [] };
};
