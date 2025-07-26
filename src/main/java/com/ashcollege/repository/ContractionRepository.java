package com.ashcollege.repository;

import com.ashcollege.entities.ContractionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContractionRepository extends JpaRepository<ContractionEntity, UUID> {

    List<ContractionEntity> findByUserId(Long userId);

    List<ContractionEntity> findByUserIdOrderByStartTimeDesc(Long userId);

    void deleteByUserId(Long userId);
}
