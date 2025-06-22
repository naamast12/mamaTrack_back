package com.ashcollege.service;

import com.ashcollege.entities.ContractionEntity;
import com.ashcollege.repository.ContractionRepository;
import com.ashcollege.responses.ContractionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
} 