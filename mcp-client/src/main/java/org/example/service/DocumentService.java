package org.example.service;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

public interface DocumentService {
    // 加载文本
    public List<Document> loadText(Resource resource, String fileName);

    // 从redis里面搜索
    public List<Document> doSearch(String question);
}
