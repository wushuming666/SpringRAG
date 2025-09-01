package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bean.ChatEntity;
import org.example.service.ChatService;
import org.example.service.DocumentService;
import org.example.utils.LeeResult;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("rag")
public class RagController {
    @Resource
    private DocumentService documentService;
    @Resource
    private ChatService chatService;

    @PostMapping("uploadRagDoc")
    public LeeResult uploadRagDoc(@RequestParam("file") MultipartFile file) {
        List<Document> documentList =  documentService.loadText(file.getResource(), file.getOriginalFilename());
        return LeeResult.ok(documentList);
    }

    @PostMapping("doSearch")
    public LeeResult doSearch(@RequestParam("question") String question) {
        List<Document> documentList = documentService.doSearch(question);
        return LeeResult.ok(documentList);
    }

    // 先到RAG向量库里面搜索，再让大模型润色下
    @PostMapping("/search")
    public void search(@RequestBody ChatEntity chatEntity, HttpServletResponse response) {

        List<Document> list = documentService.doSearch(chatEntity.getMessage());
        response.setCharacterEncoding("UTF-8");
        chatService.doChatRagSearch(chatEntity, list);
    }

}
