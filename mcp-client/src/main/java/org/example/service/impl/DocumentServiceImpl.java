package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.service.DocumentService;
import org.example.utils.CustomTextSplitter;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final RedisVectorStore redisVectorStore;

    @Override
    public List<Document> loadText(Resource resource, String fileName) {
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("fileName", fileName);
        List<Document> documentList = textReader.get();

        CustomTextSplitter tokenTextSplitter = new CustomTextSplitter();
        List<Document> list = tokenTextSplitter.apply(documentList);
        System.out.println("list = " + list);

        // 向量存储
        redisVectorStore.add(list);

        return documentList;
    }
}
