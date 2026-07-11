package cl.duoc.cdy2204.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_GUIAS = "guias.exchange";
    public static final String EXCHANGE_ERRORES = "guias.errores.exchange";

    public static final String COLA_GUIAS = "guias.procesamiento";
    public static final String COLA_ERRORES = "guias.errores";

    public static final String ROUTING_GUIAS = "guias.procesar";
    public static final String ROUTING_ERRORES = "guias.error";

    @Bean
    public DirectExchange guiasExchange() {
        return new DirectExchange(EXCHANGE_GUIAS, true, false);
    }

    @Bean
    public DirectExchange erroresExchange() {
        return new DirectExchange(EXCHANGE_ERRORES, true, false);
    }

    @Bean
    public Queue guiasQueue() {
        return QueueBuilder
                .durable(COLA_GUIAS)
                .deadLetterExchange(EXCHANGE_ERRORES)
                .deadLetterRoutingKey(ROUTING_ERRORES)
                .build();
    }

    @Bean
    public Queue erroresQueue() {
        return QueueBuilder
                .durable(COLA_ERRORES)
                .build();
    }

    @Bean
    public Binding guiasBinding(
            @Qualifier("guiasQueue") Queue guiasQueue,
            @Qualifier("guiasExchange") DirectExchange guiasExchange
    ) {
        return BindingBuilder
                .bind(guiasQueue)
                .to(guiasExchange)
                .with(ROUTING_GUIAS);
    }

    @Bean
    public Binding erroresBinding(
            @Qualifier("erroresQueue") Queue erroresQueue,
            @Qualifier("erroresExchange") DirectExchange erroresExchange
    ) {
        return BindingBuilder
                .bind(erroresQueue)
                .to(erroresExchange)
                .with(ROUTING_ERRORES);
    }
}
