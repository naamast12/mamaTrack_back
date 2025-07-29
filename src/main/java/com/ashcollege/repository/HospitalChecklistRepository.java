package com.ashcollege.repository;

import com.ashcollege.entities.HospitalChecklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalChecklistRepository extends JpaRepository<HospitalChecklistEntity, Long> {
    Optional<HospitalChecklistEntity> findByUserId(Long userId);
} 