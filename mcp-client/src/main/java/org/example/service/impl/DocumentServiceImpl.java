package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.service.DocumentService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    @Override
    public List<Document> loadText(Resource resource, String fileName) {
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("fileName", fileName);
        List<Document> documentList = textReader.get();
        return documentList;
    }
}
