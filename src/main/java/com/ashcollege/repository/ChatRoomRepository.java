// src/main/java/com/ashcollege/repository/ChatRoomRepository.java
package com.ashcollege.repository;

import com.ashcollege.entities.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    Optional<ChatRoomEntity> findByCode(String code);
}