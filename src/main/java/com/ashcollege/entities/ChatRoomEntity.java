// src/main/java/com/ashcollege/entities/ChatRoomEntity.java
package com.ashcollege.entities;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "chat_rooms")
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // תואם BIGSERIAL
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // "general", "t1", "t2", "t3"

    @Column(nullable = false)
    private String name; // "כללי", "טרימסטר 1", ...

    // בעדיפות להשתמש ב־DEFAULT now() של הדאטהבייס
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    // getters/setters
    public Long getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}