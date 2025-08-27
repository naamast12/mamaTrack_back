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
        if (recent.size() < 8) return false;  // נשאר 8 צירים

        List<ContractionEntity> lastEight = recent.subList(0, 8);

        // בדיקה שכל הצירים ארוכים מספיק (45+ שניות)
        boolean allLong = lastEight.stream().allMatch(c -> c.getDurationSeconds() >= 45);
        
        // בדיקה שהמרווחים תקינים (3-5 דקות)
        boolean allIntervalsOk = true;
        for (int i = 1; i < lastEight.size(); i++) {
            long interval = java.time.Duration.between(
                    lastEight.get(i).getStartTime(),
                    lastEight.get(i - 1).getStartTime()
            ).getSeconds();
            // מרווח של 3-5 דקות (180-300 שניות)
            if (interval < 180 || interval > 300) {
                allIntervalsOk = false;
                break;
            }
        }

        // בדיקה שכל ה-8 צירים התרחשו בטווח של 30-45 דקות
        long totalSpan = java.time.Duration.between(
                lastEight.get(7).getStartTime(),  // הציר ה-1 (הישן ביותר)
                lastEight.get(0).getStartTime()   // הציר ה-8 (החדש ביותר)
        ).toMinutes();
        boolean timeSpanOk = totalSpan <= 45;  // נשאר 45 דקות

        return allLong && allIntervalsOk && timeSpanOk;
    }

        // בדיקה אם הצירים מתחילים להיות סדירים (4 צירים)
    public boolean isPatternStarting(Long userId) {
        List<ContractionEntity> recent = contractionRepository.findByUserIdOrderByStartTimeDesc(userId);
        System.out.println("🔍 isPatternStarting - כמות צירים: " + recent.size());
        
        if (recent.size() < 4) {
            System.out.println("❌ פחות מ-4 צירים");
            return false;  // נשאר 4 צירים
        }

        List<ContractionEntity> lastFour = recent.subList(0, 4);

        // בדיקה שכל הצירים ארוכים מספיק (45+ שניות)
        boolean allLong = lastFour.stream().allMatch(c -> c.getDurationSeconds() >= 45);
        System.out.println("🔍 allLong (45+ שניות): " + allLong);
        
        // בדיקה שהמרווחים תקינים (3-5 דקות)
        boolean allIntervalsOk = true;
        for (int i = 1; i < lastFour.size(); i++) {
            long interval = java.time.Duration.between(
                    lastFour.get(i).getStartTime(),
                    lastFour.get(i - 1).getStartTime()
            ).getSeconds();
            System.out.println("🔍 מרווח " + i + ": " + interval + " שניות");
            // מרווח של 3-5 דקות (180-300 שניות)
            if (interval < 180 || interval > 300) {
                allIntervalsOk = false;
                System.out.println("❌ מרווח לא תקין: " + interval + " שניות");
                break;
            }
        }
        System.out.println("🔍 allIntervalsOk (3-5 דקות): " + allIntervalsOk);

        // בדיקה שכל ה-4 צירים התרחשו בטווח של 15-25 דקות
        long totalSpan = java.time.Duration.between(
                lastFour.get(3).getStartTime(),  // הציר ה-1 (הישן ביותר)
                lastFour.get(0).getStartTime()   // הציר ה-4 (החדש ביותר)
            ).toMinutes();
        boolean timeSpanOk = totalSpan <= 25; // נשאר 25 דקות
        System.out.println("🔍 totalSpan: " + totalSpan + " דקות, timeSpanOk: " + timeSpanOk);

        boolean result = allLong && allIntervalsOk && timeSpanOk;
        System.out.println("🔍 isPatternStarting result: " + result);
        return result;
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
