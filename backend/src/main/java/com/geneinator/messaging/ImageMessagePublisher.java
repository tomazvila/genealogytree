package com.geneinator.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishImageForProcessing(ImageProcessingMessage message) {
        log.info("Publishing image processing message for photo: {}", message.getPhotoId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.IMAGE_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_PROCESSING,
                message
        );
    }
}
