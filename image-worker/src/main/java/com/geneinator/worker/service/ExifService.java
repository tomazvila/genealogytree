package com.geneinator.worker.service;

import java.nio.file.Path;
import java.util.Map;

public interface ExifService {

    Map<String, Object> extractMetadata(Path imagePath);
}
