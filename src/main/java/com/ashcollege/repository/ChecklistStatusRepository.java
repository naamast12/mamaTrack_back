package com.ashcollege.repository;

import com.ashcollege.entities.ChecklistStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChecklistStatusRepository extends JpaRepository<ChecklistStatusEntity, Long> {
    List<ChecklistStatusEntity> findByUserId(Long userId);
    Optional<ChecklistStatusEntity> findByUserIdAndItemId(Long userId, Long itemId);
}
