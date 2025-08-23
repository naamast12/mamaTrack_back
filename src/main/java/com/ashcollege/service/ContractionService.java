package com.ashcollege.service;

import com.ashcollege.entities.ContractionEntity;
import com.ashcollege.repository.ContractionRepository;
import com.ashcollege.responses.ContractionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContractionService {
    private final ContractionRepository contractionRepository;

    @Autowired
    public ContractionService(ContractionRepository contractionRepository) {
        this.contractionRepository = contractionRepository;
    }

    public ContractionDto saveContraction(ContractionDto dto) {
        ContractionEntity entity = new ContractionEntity();
        entity.setUserId(dto.getUserId());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setDurationSeconds(dto.getDurationSeconds());
        entity.setCreatedAt(LocalDateTime.now());

        ContractionEntity saved = contractionRepository.save(entity);

        boolean shouldGo = shouldGoToHospital(dto.getUserId());
        System.out.println("🚗 shouldGoToHospital = " + shouldGo);

        return toDto(saved);
    }

    public List<ContractionDto> getContractionsByUserId(Long userId) {
        List<ContractionEntity> entities = contractionRepository.findByUserId(userId);
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    private ContractionDto toDto(ContractionEntity entity) {
        return new ContractionDto(
                entity.getId(),
                entity.getUserId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getDurationSeconds(),
                entity.getCreatedAt()
        );
    }

    public boolean shouldGoToHospital(Long userId) {
        List<ContractionEntity> recent = contractionRepository.findByUserIdOrderByStartTimeDesc(userId);
        if (recent.size() < 4) return false;

        List<ContractionEntity> lastFour = recent.subList(0, 4);

        boolean allLong = lastFour.stream().allMatch(c -> c.getDurationSeconds() > 45);
        boolean allIntervalsOk = true;
        for (int i = 1; i < lastFour.size(); i++) {
            long interval = java.time.Duration.between(
                    lastFour.get(i).getStartTime(),
                    lastFour.get(i - 1).getStartTime()
            ).getSeconds();
            if (interval < 180 || interval > 300) {
                allIntervalsOk = false;
                break;
            }
        }

        long totalSpan = java.time.Duration.between(
                lastFour.get(3).getStartTime(),
                lastFour.get(0).getStartTime()
        ).toMinutes();
        boolean timeSpanOk = totalSpan <= 60;  // שינוי מ-30 ל-60 דקות

        return allLong && allIntervalsOk && timeSpanOk;
    }

    // בדיקה אם הצירים מתחילים להיות סדירים (3 צירים)
    public boolean isPatternStarting(Long userId) {
        List<ContractionEntity> recent = contractionRepository.findByUserIdOrderByStartTimeDesc(userId);
        if (recent.size() < 3) return false;

        List<ContractionEntity> lastThree = recent.subList(0, 3);

        boolean allLong = lastThree.stream().allMatch(c -> c.getDurationSeconds() > 45);
        boolean allIntervalsOk = true;
        for (int i = 1; i < lastThree.size(); i++) {
            long interval = java.time.Duration.between(
                    lastThree.get(i).getStartTime(),
                    lastThree.get(i - 1).getStartTime()
            ).getSeconds();
            if (interval < 180 || interval > 300) {
                allIntervalsOk = false;
                break;
            }
        }

        long totalSpan = java.time.Duration.between(
                lastThree.get(2).getStartTime(),
                lastThree.get(0).getStartTime()
        ).toMinutes();
        boolean timeSpanOk = totalSpan <= 45; // 45 דקות ל-3 צירים

        return allLong && allIntervalsOk && timeSpanOk;
    }

    // בדיקה אם הצירים לא סדירים (2-3 צירים שלא עומדים בקריטריונים)
    public boolean isPatternIrregular(Long userId) {
        List<ContractionEntity> recent = contractionRepository.findByUserIdOrderByStartTimeDesc(userId);
        if (recent.size() < 2) return false;

        // אם יש 2-3 צירים אבל הם לא עומדים בקריטריונים של דפוס סדיר
        if (recent.size() >= 2 && recent.size() <= 3) {
            List<ContractionEntity> lastFew = recent.subList(0, recent.size());
            
            // בדיקה אם יש בעיה עם משך הצירים
            boolean hasShortContractions = lastFew.stream().anyMatch(c -> c.getDurationSeconds() <= 45);
            
            // בדיקה אם יש בעיה עם המרווחים
            boolean hasBadIntervals = false;
            for (int i = 1; i < lastFew.size(); i++) {
                long interval = java.time.Duration.between(
                        lastFew.get(i).getStartTime(),
                        lastFew.get(i - 1).getStartTime()
                ).getSeconds();
                if (interval < 180 || interval > 300) {
                    hasBadIntervals = true;
                    break;
                }
            }
            
            return hasShortContractions || hasBadIntervals;
        }
        
        return false;
    }

    @Transactional
    public void deleteAllByUserId(Long userId) {
        contractionRepository.deleteByUserId(userId);
    }
}
