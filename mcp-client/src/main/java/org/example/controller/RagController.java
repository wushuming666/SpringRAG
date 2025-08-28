package org.example.controller;

import jakarta.annotation.Resource;
import org.example.service.ChatService;
import org.example.service.DocumentService;
import org.example.utils.LeeResult;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

}
