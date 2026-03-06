package com.geneinator.worker.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

@Configuration
public class RabbitMQConfig {

    public static final String IMAGE_PROCESSING_QUEUE = "image.processing";
    public static final String IMAGE_PROCESSING_DLQ = "image.processing.dead-letter";
    public static final String IMAGE_DLX_EXCHANGE = "image.dlx.exchange";

    @Value("${app.processing.max-concurrent:3}")
    private int maxConcurrent;

    @Value("${app.rabbitmq.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${app.rabbitmq.retry.initial-interval:1000}")
    private long initialInterval;

    @Value("${app.rabbitmq.retry.multiplier:2.0}")
    private double multiplier;

    @Value("${app.rabbitmq.retry.max-interval:10000}")
    private long maxInterval;

    @Bean
    @SuppressWarnings("removal")
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // Dead Letter Exchange
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(IMAGE_DLX_EXCHANGE);
    }

    // Dead Letter Queue
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(IMAGE_PROCESSING_DLQ).build();
    }

    // Main queue with DLQ binding
    @Bean
    public Queue imageProcessingQueue() {
        return QueueBuilder.durable(IMAGE_PROCESSING_QUEUE)
                .withArgument("x-dead-letter-exchange", IMAGE_DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", IMAGE_PROCESSING_DLQ)
                .build();
    }

    // Binding DLQ to DLX
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(IMAGE_PROCESSING_DLQ);
    }

    // Retry template with exponential backoff
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(initialInterval);
        backOffPolicy.setMultiplier(multiplier);
        backOffPolicy.setMaxInterval(maxInterval);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxRetryAttempts);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    // Retry interceptor that sends to DLQ after retries exhausted
    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return org.springframework.retry.interceptor.RetryInterceptorBuilder.stateless()
                .retryOperations(retryTemplate())
                .recoverer((args, cause) -> {
                    // After max retries, message will be rejected and go to DLQ
                    throw new org.springframework.amqp.AmqpRejectAndDontRequeueException(
                            "Retry exhausted for message", cause);
                })
                .build();
    }

    // Listener container factory with retry and concurrency
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            RetryOperationsInterceptor retryInterceptor) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(maxConcurrent);
        factory.setPrefetchCount(maxConcurrent);
        factory.setDefaultRequeueRejected(false); // Don't requeue on failure, send to DLQ
        factory.setAdviceChain(retryInterceptor);
        return factory;
    }
}
