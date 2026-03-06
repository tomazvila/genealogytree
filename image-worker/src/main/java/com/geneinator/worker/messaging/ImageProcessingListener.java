package com.geneinator.worker.messaging;

import com.geneinator.worker.config.RabbitMQConfig;
import com.geneinator.worker.entity.Photo;
import com.geneinator.worker.repository.PhotoRepository;
import com.geneinator.worker.service.ImageProcessingResult;
import com.geneinator.worker.service.ImageProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageProcessingListener {

    private final ImageProcessingService imageProcessingService;
    private final PhotoRepository photoRepository;

    @RabbitListener(queues = RabbitMQConfig.IMAGE_PROCESSING_QUEUE)
    @Transactional
    public void handleImageProcessing(ImageProcessingMessage message) {
        log.info("Received image processing request for photo: {}", message.getPhotoId());

        Photo photo = photoRepository.findById(message.getPhotoId())
                .orElseThrow(() -> new IllegalStateException("Photo not found: " + message.getPhotoId()));

        // Set status to PROCESSING
        photo.setProcessingStatus(Photo.ProcessingStatus.PROCESSING);
        photoRepository.save(photo);

        try {
            ImageProcessingResult result = imageProcessingService.processImage(message);

            // Update photo with processing results
            photo.setProcessingStatus(Photo.ProcessingStatus.COMPLETED);
            photo.setThumbnailSmall(result.getThumbnailSmall());
            photo.setThumbnailMedium(result.getThumbnailMedium());
            photo.setThumbnailLarge(result.getThumbnailLarge());
            photo.setExifData(result.getExifData());
            photoRepository.save(photo);

            log.info("Successfully processed photo: {}", message.getPhotoId());
        } catch (Exception e) {
            log.error("Failed to process photo: {}", message.getPhotoId(), e);

            // Mark as failed
            photo.setProcessingStatus(Photo.ProcessingStatus.FAILED);
            photoRepository.save(photo);

            throw e; // Re-throw to trigger retry/DLQ
        }
    }
}
