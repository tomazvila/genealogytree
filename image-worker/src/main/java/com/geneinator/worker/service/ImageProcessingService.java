package com.geneinator.worker.service;

import com.geneinator.worker.messaging.ImageProcessingMessage;

public interface ImageProcessingService {

    ImageProcessingResult processImage(ImageProcessingMessage message);
}
