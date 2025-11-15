package com.aziz.library.infrastructure.adapter.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.aziz.library.domain.model.Article;
import com.aziz.library.domain.port.out.ArticleRepositoryPort;
import com.aziz.library.infrastructure.adapter.persistence.entity.ArticleEntity;
import com.aziz.library.infrastructure.adapter.persistence.repository.ArticleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArticleRepositoryAdapter implements ArticleRepositoryPort{

    private final ArticleRepository articleRepository;
    
    @Override
    public Article save(Article article) {
        ArticleEntity entity = toEntity(article);
        ArticleEntity saved = articleRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public Optional<Article> findById(Long id) {
        return articleRepository.findById(id).map(this::toDomain);
    }
    
    @Override
    public List<Article> findAll() {
        return articleRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Article> findByAuthorId(Long authorId) {
        return articleRepository.findByAuthorId(authorId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Article> findAllPublic() {
        return articleRepository.findByIsPublicTrue().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(Long id) {
        articleRepository.deleteById(id);
    }
    
    private Article toDomain(ArticleEntity entity) {
        return Article.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .authorId(entity.getAuthorId())
                .isPublic(entity.isPublic())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    private ArticleEntity toEntity(Article domain) {
        return ArticleEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .content(domain.getContent())
                .authorId(domain.getAuthorId())
                .isPublic(domain.isPublic())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

}
