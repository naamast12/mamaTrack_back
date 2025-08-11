package com.ashcollege.service;

import com.ashcollege.entities.ChatMessageEntity;
import com.ashcollege.entities.ChatRoomEntity;
import com.ashcollege.entities.UserEntity;
import com.ashcollege.repository.ChatMessageRepository;
import com.ashcollege.repository.ChatRoomRepository;
import com.ashcollege.repository.UserRepository; // ← חדש
import org.springframework.security.access.AccessDeniedException; // ← חדש
import org.springframework.security.core.Authentication;           // ← חדש
import org.springframework.security.core.context.SecurityContextHolder; // ← חדש
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Service
public class ChatService {

    private final ChatRoomRepository roomRepo;
    private final ChatMessageRepository msgRepo;
    private final UserRepository userRepo; // ← חדש

    private static final int DEFAULT_LIMIT = 30;
    private static final int MAX_LIMIT = 100;

    // הוספנו UserRepository לקונסטרקטור
    public ChatService(ChatRoomRepository roomRepo,
                       ChatMessageRepository msgRepo,
                       UserRepository userRepo) {
        this.roomRepo = roomRepo;
        this.msgRepo = msgRepo;
        this.userRepo = userRepo; // ← חדש
    }

    public List<ChatRoomEntity> listRooms() {
        return roomRepo.findAll(); // אין שינוי
    }

    public List<ChatMessageEntity> getMessages(Long roomId, Integer limit, Long beforeId, Long afterId) {
        int lim = normalizeLimit(limit);

        // בדיקת הרשאה לקריאה לפי קוד חדר וטרימסטר המשתמש המחובר
        ChatRoomEntity room = roomRepo.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("room not found"));
        int trimester = calcCurrentUserTrimester(); // לפי ה-JWT
        assertRoomAccess(room, trimester);          // 403 אם לא מורשה

        if (afterId != null) return msgRepo.findAfterIdAsc(roomId, afterId, lim);

        if (beforeId != null) {
            List<ChatMessageEntity> desc = msgRepo.findBeforeIdDesc(roomId, beforeId, lim);
            Collections.reverse(desc);
            return desc;
        }

        List<ChatMessageEntity> lastDesc = msgRepo.findLastNDesc(roomId, lim);
        Collections.reverse(lastDesc);
        return lastDesc;
    }

    @Transactional
    public ChatMessageEntity sendMessage(Long roomId, Long senderId, String body) {
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("message body is empty");
        }
        ChatRoomEntity room = roomRepo.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("room not found"));

        // בדיקת הרשאה לכתיבה לפי קוד חדר וטרימסטר השולחת
        int trimester = calcUserTrimester(senderId);
        assertRoomAccess(room, trimester); // 403 אם לא מורשה

        ChatMessageEntity msg = new ChatMessageEntity();
        msg.setRoom(room);
        msg.setSenderId(senderId);
        msg.setBody(body.trim());
        return msgRepo.save(msg);
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null || limit <= 0) return DEFAULT_LIMIT;
        return Math.min(limit, MAX_LIMIT);
    }


    // ===== עזר: טרימסטר של המשתמש המחובר (mail מתוך ה-JWT) =====
    private int calcCurrentUserTrimester() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null)
            throw new AccessDeniedException("Unauthorized");
        String mail = auth.getName();
        UserEntity u = userRepo.findByMail(mail);
        if (u == null) throw new AccessDeniedException("User not found");
        return calcTrimesterFromUser(u);
    }

    // ===== עזר: טרימסטר לפי userId שנשלח מה-Controller =====
    private int calcUserTrimester(Long userId) {
        UserEntity u = userRepo.findById(userId)
                .orElseThrow(() -> new AccessDeniedException("User not found"));
        return calcTrimesterFromUser(u);
    }

    // ===== חישוב טרימסטר בפועל =====
    // חישוב טרימסטר בלי לגעת בטבלה (ללא pregnancyWeek)
    private int calcTrimesterFromUser(UserEntity u) {
        // 1) אם יש תאריך וסת אחרון – מחשבות שבועות ממנו
        if (u.getLastPeriodDate() != null) {
            int weeks = (int) java.time.temporal.ChronoUnit.WEEKS.between(
                    u.getLastPeriodDate(), java.time.LocalDate.now());
            return weekToTrimester(weeks);
        }

        // 2) אם אין LPD אבל יש תאריך לידה משוער – נגזור ממנו את השבוע
        if (u.getEstimatedDueDate() != null) {
            long daysToDue = java.time.temporal.ChronoUnit.DAYS.between(
                    java.time.LocalDate.now(), u.getEstimatedDueDate());
            int week = 40 - (int) Math.round(daysToDue / 7.0); // קירוב לשבוע נוכחי
            return weekToTrimester(week);
        }

        // 3) אין נתונים → רק general
        return 0;
    }

    private int weekToTrimester(Integer week) {
        if (week == null || week < 1) return 0;
        if (week <= 12) return 1;   // 1–12
        if (week <= 27) return 2;   // 13–27
        return 3;                   // 28–40
    }

    // ===== אכיפת גישה לחדרים =====
    private void assertRoomAccess(ChatRoomEntity room, int userTrimester) {
        String code = room.getCode(); // "general" | "t1" | "t2" | "t3"
        if ("general".equals(code)) return; // תמיד מותר

        if ("t1".equals(code) && userTrimester != 1)
            throw new AccessDeniedException("Only trimester 1 users can access this room");
        if ("t2".equals(code) && userTrimester != 2)
            throw new AccessDeniedException("Only trimester 2 users can access this room");
        if ("t3".equals(code) && userTrimester != 3)
            throw new AccessDeniedException("Only trimester 3 users can access this room");
    }
}