package cl.duoc.cdy2204.messaging.producer;

import cl.duoc.cdy2204.config.RabbitMqConfig;
import cl.duoc.cdy2204.messaging.dto.GuiaMensaje;
import cl.duoc.cdy2204.model.GuiaDespacho;
import java.nio.charset.StandardCharsets;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class GuiaMensajeProducer {

    private final RabbitTemplate rabbitTemplate;

    public GuiaMensajeProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public GuiaMensaje enviar(
            GuiaDespacho guia,
            String tipoEvento,
            String destinatario,
            String direccionDestino,
            String detallePedido
    ) {
        GuiaMensaje mensaje = crearMensaje(
                guia,
                tipoEvento,
                destinatario,
                direccionDestino,
                detallePedido
        );

        Message mensajeRabbit = MessageBuilder
                .withBody(mensaje.toJson().getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setContentEncoding(StandardCharsets.UTF_8.name())
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setMessageId(mensaje.getMensajeId())
                .setHeader("tipoEvento", mensaje.getTipoEvento())
                .setHeader("numeroGuia", mensaje.getNumeroGuia())
                .build();

        rabbitTemplate.send(
                RabbitMqConfig.EXCHANGE_GUIAS,
                RabbitMqConfig.ROUTING_GUIAS,
                mensajeRabbit
        );

        System.out.println(
                "Mensaje enviado a RabbitMQ. mensajeId="
                        + mensaje.getMensajeId()
                        + ", numeroGuia="
                        + mensaje.getNumeroGuia()
                        + ", evento="
                        + mensaje.getTipoEvento()
        );

        return mensaje;
    }

    private GuiaMensaje crearMensaje(
            GuiaDespacho guia,
            String tipoEvento,
            String destinatario,
            String direccionDestino,
            String detallePedido
    ) {
        GuiaMensaje mensaje = new GuiaMensaje();
        mensaje.setGuiaId(guia.getId());
        mensaje.setNumeroGuia(guia.getNumeroGuia());
        mensaje.setTransportista(guia.getTransportista());
        mensaje.setFechaGuia(guia.getFechaGuia());
        mensaje.setArchivoNombre(guia.getArchivoNombre());
        mensaje.setRutaEfs(guia.getRutaEfs());
        mensaje.setRutaS3(guia.getRutaS3());
        mensaje.setEstado(guia.getEstado());
        mensaje.setDestinatario(destinatario);
        mensaje.setDireccionDestino(direccionDestino);
        mensaje.setDetallePedido(detallePedido);
        mensaje.setTipoEvento(tipoEvento);
        return mensaje;
    }
}
