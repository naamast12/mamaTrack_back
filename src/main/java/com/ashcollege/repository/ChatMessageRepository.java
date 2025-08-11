// src/main/java/com/ashcollege/repository/ChatMessageRepository.java
package com.ashcollege.repository;

import com.ashcollege.entities.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    // טעינה ראשונה: 30 האחרונות (נחזיר מהשירות בסדר עולה)
    @Query(value = """
        select * from chat_messages
        where room_id = :roomId
        order by id desc
        limit :limit
        """, nativeQuery = true)
    List<ChatMessageEntity> findLastNDesc(@Param("roomId") Long roomId, @Param("limit") int limit);

    // גלילה למעלה: ישנות יותר מ-beforeId (מחזיר בסדר יורד – נהפוך בשירות)
    @Query(value = """
        select * from chat_messages
        where room_id = :roomId and id < :beforeId
        order by id desc
        limit :limit
        """, nativeQuery = true)
    List<ChatMessageEntity> findBeforeIdDesc(@Param("roomId") Long roomId,
                                             @Param("beforeId") Long beforeId,
                                             @Param("limit") int limit);

    // polling לחדשות: חדשות יותר מ-afterId (כבר מסודר עולה)
    @Query(value = """
        select * from chat_messages
        where room_id = :roomId and id > :afterId
        order by id asc
        limit :limit
        """, nativeQuery = true)
    List<ChatMessageEntity> findAfterIdAsc(@Param("roomId") Long roomId,
                                           @Param("afterId") Long afterId,
                                           @Param("limit") int limit);
}