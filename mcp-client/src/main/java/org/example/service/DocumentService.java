package org.example.service;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DocumentService {
    public List<Document> loadText(Resource resource, String fileName);
}
