package com.aziz.library.domain.port.out;

import java.util.List;
import java.util.Optional;

import com.aziz.library.domain.model.Article;

public interface ArticleRepositoryPort {
    Article save(Article article);
    Optional<Article> findById(Long id);
    List<Article> findAll();
    List<Article> findByAuthorId(Long authorId);
    List<Article> findAllPublic();
    void deleteById(Long id);
}
