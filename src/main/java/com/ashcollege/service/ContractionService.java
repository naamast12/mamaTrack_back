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
        boolean timeSpanOk = totalSpan <= 30;

        return allLong && allIntervalsOk && timeSpanOk;
    }

    @Transactional
    public void deleteAllByUserId(Long userId) {
        contractionRepository.deleteByUserId(userId);
    }
}
