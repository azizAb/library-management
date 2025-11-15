package com.aziz.library.infrastructure.adapter.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aziz.library.infrastructure.adapter.persistence.entity.OtpTokenEntity;

@Repository
public interface OtpRepository extends JpaRepository<OtpTokenEntity, Long>{
    Optional<OtpTokenEntity> findByUserIdAndToken(Long userId, String token);
    
    @Query("SELECT o FROM OtpTokenEntity o WHERE o.userId = :userId ORDER BY o.createdAt DESC LIMIT 1")
    Optional<OtpTokenEntity> findLatestByUserId(Long userId);
    
    void deleteByUserId(Long userId);
}
