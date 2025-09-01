package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bean.ChatEntity;
import org.example.service.ChatService;
import org.example.service.SesrXngService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("internet")
public class InternetController {
    @Resource
    private SesrXngService sesrXngService;

    @Resource
    private ChatService chatService;

    @GetMapping("/test")
    public Object test(@RequestParam("query") String query){
        return sesrXngService.search(query);
    }

    @PostMapping("/search")
    public void search(@RequestBody ChatEntity chatEntity, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        chatService.doInternetSearch(chatEntity);
    }
}
