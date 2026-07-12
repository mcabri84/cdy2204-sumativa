package cl.duoc.cdy2204.messaging.consumer;

import cl.duoc.cdy2204.service.OracleGuiaProcesadaService;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class GuiaMensajeConsumer {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GuiaMensajeConsumer.class);

    private final OracleGuiaProcesadaService oracleService;

    public GuiaMensajeConsumer(
            OracleGuiaProcesadaService oracleService) {

        this.oracleService = oracleService;
    }

    @RabbitListener(queues = "guias.procesamiento")
    public void consumir(Message mensajeRabbit) {
        String payload = new String(
                mensajeRabbit.getBody(),
                StandardCharsets.UTF_8
        );

        try {
            String resultado =
                    oracleService.guardarMensaje(payload);

            LOGGER.info(
                    "Mensaje RabbitMQ procesado en Oracle Cloud. Resultado={}",
                    resultado
            );

        } catch (Exception e) {
            LOGGER.error(
                    "Error procesando mensaje. Se enviara a guias.errores",
                    e
            );

            throw new AmqpRejectAndDontRequeueException(
                    "No fue posible guardar la guia en Oracle Cloud",
                    e
            );
        }
    }
}
