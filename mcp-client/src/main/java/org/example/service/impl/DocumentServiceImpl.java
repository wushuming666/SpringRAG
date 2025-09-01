package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.DocumentService;
import org.example.utils.CustomTextSplitter;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final RedisVectorStore redisVectorStore;

    @Override
    public List<Document> loadText(Resource resource, String fileName) {
        log.info("开始上传文档");
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("fileName", fileName);
        List<Document> documentList = textReader.get();

        CustomTextSplitter tokenTextSplitter = new CustomTextSplitter();
        List<Document> list = tokenTextSplitter.apply(documentList);
        //把list里面的数据依次打印出来
        list.forEach(document -> log.info("Document: {}", document));

        // 向量存储
        redisVectorStore.add(list);
        log.info("文档上传redis成功");
        return documentList;
    }

    @Override
    public List<Document> doSearch(String question) {
        return redisVectorStore.similaritySearch(question);
    }
}
