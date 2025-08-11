// src/main/java/com/ashcollege/dto/ChatDtos.java
package com.ashcollege.dto;

import com.ashcollege.entities.ChatMessageEntity;
import com.ashcollege.entities.ChatRoomEntity;
import com.fasterxml.jackson.annotation.JsonAlias;  // ← הוסיפי


import java.time.OffsetDateTime;

public class ChatDtos {

    public static class SendMessageReq {
        private String body;


        @JsonAlias({"parentId", "parent_id"})   // ← מקבל גם וגם
        private Long parentId;

        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }

        public Long getParentId() { return parentId; }     // ← חדש
        public void setParentId(Long parentId) { this.parentId = parentId; } // ← חדש
    }

    public static class RoomDto {
        public Long id;
        public String code;
        public String name;
        public static RoomDto from(ChatRoomEntity r) {
            RoomDto d = new RoomDto();
            d.id = r.getId(); d.code = r.getCode(); d.name = r.getName();
            return d;
        }
    }

    public static class MessageDto {
        public Long id;
        public Long senderId;
        public String body;
        public OffsetDateTime createdAt;
        public Long parentId; // ← חדש (נוח להצגה בעתיד)

        public static MessageDto from(ChatMessageEntity m) {
            MessageDto d = new MessageDto();
            d.id = m.getId();
            d.senderId = m.getSenderId();
            d.body = m.getBody();
            d.createdAt = m.getCreatedAt();
            d.parentId = (m.getParent() != null ? m.getParent().getId() : null); // ← חדש
            return d;
        }
    }
}