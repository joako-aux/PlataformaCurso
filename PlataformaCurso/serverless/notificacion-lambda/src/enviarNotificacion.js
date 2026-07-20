// Función serverless PRODUCTORA.
// Se expone vía API Gateway (POST /notificaciones) y encola el
// mensaje recibido en la cola SQS, de forma asíncrona (IE10, IE11, IE14).

const { SQSClient, SendMessageCommand } = require("@aws-sdk/client-sqs");

const sqs = new SQSClient({ region: process.env.AWS_REGION || "us-east-1" });

exports.handler = async (event) => {
  try {
    const body = event.body ? JSON.parse(event.body) : {};

    const { usuarioId, curso, mensaje } = body;

    if (!usuarioId || !mensaje) {
      return {
        statusCode: 400,
        body: JSON.stringify({ error: "usuarioId y mensaje son obligatorios" }),
      };
    }

    const payload = {
      usuarioId,
      curso: curso || null,
      mensaje,
      timestamp: new Date().toISOString(),
    };

    await sqs.send(
      new SendMessageCommand({
        QueueUrl: process.env.QUEUE_URL,
        MessageBody: JSON.stringify(payload),
      })
    );

    return {
      statusCode: 202,
      body: JSON.stringify({ status: "encolado", payload }),
    };
  } catch (err) {
    console.error("Error al encolar notificación:", err);
    return {
      statusCode: 500,
      body: JSON.stringify({ error: "Error interno al encolar la notificación" }),
    };
  }
};
