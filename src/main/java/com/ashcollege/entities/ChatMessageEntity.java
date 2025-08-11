// src/main/java/com/ashcollege/entities/ChatMessageEntity.java
package com.ashcollege.entities;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "chat_messages",
        indexes = {
                @Index(name = "idx_chat_messages_room_id_id", columnList = "room_id,id")
        }
)
public class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // תואם BIGSERIAL
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id")
    private ChatRoomEntity room;

    @Column(name = "sender_id", nullable = false)
    private Long senderId; // יתממשק ל-UserEntity אצלך (לפי id)

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    // להשתמש ב־DEFAULT now() מה־DB
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;


    // ← חדש: הודעת-אם (null = הודעה ראשית / "פוסט")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private ChatMessageEntity parent;


    // getters/setters
    public Long getId() { return id; }
    public ChatRoomEntity getRoom() { return room; }
    public void setRoom(ChatRoomEntity room) { this.room = room; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public OffsetDateTime getCreatedAt() { return createdAt; }


    // ← גטר/סטר חדשים
    public ChatMessageEntity getParent() {
        return parent;
    }
    public void setParent(ChatMessageEntity parent) {
        this.parent = parent;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now(); // ממלאים זמן רגע לפני ה-INSERT
        }
    }
}