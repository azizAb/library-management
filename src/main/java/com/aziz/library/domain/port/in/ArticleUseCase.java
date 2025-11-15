package com.aziz.library.domain.port.in;

import java.util.List;

import com.aziz.library.domain.model.Article;

public interface ArticleUseCase {
    Article createArticle(Article article, Long currentUserId);
    Article updateArticle(Long id, Article article, Long currentUserId);
    void deleteArticle(Long id, Long currentUserId);
    Article getArticleById(Long id, Long currentUserId);
    List<Article> getAllArticles(Long currentUserId);
    List<Article> getMyArticles(Long currentUserId);
}
