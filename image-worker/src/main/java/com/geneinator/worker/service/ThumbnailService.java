package com.geneinator.worker.service;

import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public interface ThumbnailService {

    Map<String, String> generateThumbnails(Path originalPath, UUID photoId);

    Path generateThumbnail(Path originalPath, int size, String suffix);
}
