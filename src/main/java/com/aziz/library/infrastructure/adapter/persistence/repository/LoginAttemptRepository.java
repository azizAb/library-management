package com.aziz.library.infrastructure.adapter.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aziz.library.infrastructure.adapter.persistence.entity.LoginAttemptEntity;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttemptEntity, Long>{
    List<LoginAttemptEntity> findByIdentifierAndAttemptTimeAfter(String identifier, LocalDateTime after);
    
    @Query("SELECT COUNT(l) FROM LoginAttemptEntity l WHERE l.identifier = :identifier " +
           "AND l.successful = false AND l.attemptTime > :after")
    int countFailedAttempts(@Param("identifier") String identifier, 
                            @Param("after") LocalDateTime after);
    
    void deleteByAttemptTimeBefore(LocalDateTime before);
}
