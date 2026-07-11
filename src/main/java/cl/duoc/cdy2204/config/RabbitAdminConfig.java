package cl.duoc.cdy2204.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitAdminConfig {

    @Bean
    public ApplicationRunner inicializarRabbitMq(AmqpAdmin amqpAdmin) {
        return args -> amqpAdmin.initialize();
    }
}
