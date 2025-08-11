// src/main/java/com/ashcollege/controllers/ChatController.java
package com.ashcollege.controllers;

import com.ashcollege.dto.ChatDtos.MessageDto;
import com.ashcollege.dto.ChatDtos.RoomDto;
import com.ashcollege.dto.ChatDtos.SendMessageReq;
import com.ashcollege.entities.ChatMessageEntity;
import com.ashcollege.entities.UserEntity;
import com.ashcollege.repository.UserRepository;
import com.ashcollege.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    public ChatController(ChatService chatService, UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    // 1) רשימת חדרים (כללי + טרימסטרים)
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDto>> rooms() {
        var rooms = chatService.listRooms().stream()
                .map(RoomDto::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rooms);
    }

    // 2) שליפת הודעות בחדר (טעינה ראשונה / גלילה למעלה / חדשות)
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<MessageDto>> messages(@PathVariable Long roomId,
                                                     @RequestParam(required = false) Integer limit,
                                                     @RequestParam(required = false) Long beforeId,
                                                     @RequestParam(required = false) Long afterId) {
        var msgs = chatService.getMessages(roomId, limit, beforeId, afterId)
                .stream().map(MessageDto::from).collect(Collectors.toList());
        return ResponseEntity.ok(msgs);
    }

    // 3) שליחת הודעה (JSON: { "body": "טקסט..." })
    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<MessageDto> send(@PathVariable Long roomId,
                                           @RequestBody SendMessageReq req) {
        Long senderId = getCurrentUserId();     // ← מזהה שולחת לפי המייל שב‑JWT
        ChatMessageEntity saved = chatService.sendMessage(roomId, senderId, req.getBody());
        return new ResponseEntity<>(MessageDto.from(saved), HttpStatus.CREATED);
    }

    // ===== helper =====
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Unauthorized");
        }
        String mail = auth.getName(); // ה-Principal אצלך הוא המייל
        UserEntity user = userRepository.findByMail(mail);
        if (user == null) {
            throw new RuntimeException("User not found for mail: " + mail);
        }
        return user.getId();
    }
}