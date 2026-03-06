package com.geneinator.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String store(MultipartFile file, String directory) throws IOException;

    void delete(String path) throws IOException;

    String getUrl(String path);
}
