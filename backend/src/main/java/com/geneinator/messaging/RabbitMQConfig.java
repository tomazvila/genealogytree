package com.geneinator.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String IMAGE_UPLOAD_QUEUE = "image.upload";
    public static final String IMAGE_PROCESSING_QUEUE = "image.processing";
    public static final String IMAGE_PROCESSING_DLQ = "image.processing.dead-letter";

    public static final String IMAGE_EXCHANGE = "image.exchange";
    public static final String IMAGE_DLX_EXCHANGE = "image.dlx.exchange";

    public static final String ROUTING_KEY_UPLOAD = "image.upload";
    public static final String ROUTING_KEY_PROCESSING = "image.processing";

    @Bean
    public Queue imageUploadQueue() {
        return QueueBuilder.durable(IMAGE_UPLOAD_QUEUE).build();
    }

    @Bean
    public Queue imageProcessingQueue() {
        return QueueBuilder.durable(IMAGE_PROCESSING_QUEUE)
                .withArgument("x-dead-letter-exchange", IMAGE_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", IMAGE_PROCESSING_DLQ)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(IMAGE_PROCESSING_DLQ).build();
    }

    @Bean
    public DirectExchange imageExchange() {
        return new DirectExchange(IMAGE_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(IMAGE_DLX_EXCHANGE);
    }

    @Bean
    public Binding uploadBinding(Queue imageUploadQueue, DirectExchange imageExchange) {
        return BindingBuilder.bind(imageUploadQueue).to(imageExchange).with(ROUTING_KEY_UPLOAD);
    }

    @Bean
    public Binding processingBinding(Queue imageProcessingQueue, DirectExchange imageExchange) {
        return BindingBuilder.bind(imageProcessingQueue).to(imageExchange).with(ROUTING_KEY_PROCESSING);
    }

    @Bean
    public Binding dlqBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(IMAGE_PROCESSING_DLQ);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
