package com.ashcollege.repository;

import com.ashcollege.entities.BabyChecklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BabyChecklistRepository extends JpaRepository<BabyChecklistEntity, Long> {
    Optional<BabyChecklistEntity> findByUserId(Long userId);
}