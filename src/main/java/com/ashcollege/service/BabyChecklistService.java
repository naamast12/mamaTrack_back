package com.ashcollege.service;

import com.ashcollege.entities.BabyChecklistEntity;
import com.ashcollege.entities.UserEntity;
import com.ashcollege.repository.BabyChecklistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BabyChecklistService {
    private final BabyChecklistRepository babyChecklistRepository;
    private final UserService userService;

    @Autowired
    public BabyChecklistService(BabyChecklistRepository babyChecklistRepository, UserService userService) {
        this.babyChecklistRepository = babyChecklistRepository;
        this.userService = userService;
    }

    public BabyChecklistEntity saveChecklist(String itemsStatusJson) {
        Long userId = getCurrentUserId();

        Optional<BabyChecklistEntity> existing = babyChecklistRepository.findByUserId(userId);

        if (existing.isPresent()) {
            BabyChecklistEntity entity = existing.get();
            entity.setItemsStatus(itemsStatusJson);
            return babyChecklistRepository.save(entity);
        } else {
            BabyChecklistEntity entity = new BabyChecklistEntity();
            entity.setUserId(userId);
            entity.setItemsStatus(itemsStatusJson);
            return babyChecklistRepository.save(entity);
        }
    }

    public BabyChecklistEntity getChecklistForCurrentUser() {
        Long userId = getCurrentUserId();
        return babyChecklistRepository.findByUserId(userId).orElse(null);
    }

    public BabyChecklistEntity resetChecklist() {
        Long userId = getCurrentUserId();
        String resetJson = createResetJson();

        Optional<BabyChecklistEntity> existing = babyChecklistRepository.findByUserId(userId);

        if (existing.isPresent()) {
            BabyChecklistEntity entity = existing.get();
            entity.setItemsStatus(resetJson);
            return babyChecklistRepository.save(entity);
        } else {
            BabyChecklistEntity entity = new BabyChecklistEntity();
            entity.setUserId(userId);
            entity.setItemsStatus(resetJson);
            return babyChecklistRepository.save(entity);
        }
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        Object principal = auth.getPrincipal();
        System.out.println("🔐 principal = " + principal);

        try {
            if (principal instanceof Integer) {
                return ((Integer) principal).longValue();
            } else if (principal instanceof String) {
                String value = (String) principal;

                try {
                    return Long.parseLong(value); // אם זה ID כמחרוזת
                } catch (NumberFormatException ignored) {}

                UserEntity user = userService.findByMail(value);
                if (user == null) {
                    System.err.println("❌ User not found for mail: " + value);
                    throw new IllegalStateException("User not found");
                }
                return user.getId();
            } else {
                throw new IllegalStateException("Unknown principal type: " + principal.getClass());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to extract user ID from principal");
        }
    }

    private String createResetJson() {
        // כאן תחזירי את ה־JSON הריק או המוגדר כברירת מחדל
        return "[ ... ]";
    }
}
