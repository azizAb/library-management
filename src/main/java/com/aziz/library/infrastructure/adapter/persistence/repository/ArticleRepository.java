package com.aziz.library.infrastructure.adapter.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aziz.library.infrastructure.adapter.persistence.entity.ArticleEntity;

@Repository
public interface ArticleRepository extends JpaRepository <ArticleEntity, Long>{
    List<ArticleEntity> findByAuthorId(Long authorId);
    List<ArticleEntity> findByIsPublicTrue();
}
