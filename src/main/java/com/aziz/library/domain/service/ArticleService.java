package com.aziz.library.domain.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.aziz.library.domain.exception.ArticleNotFoundException;
import com.aziz.library.domain.exception.UnauthorizedException;
import com.aziz.library.domain.exception.UserNotFoundException;
import com.aziz.library.domain.model.Article;
import com.aziz.library.domain.model.Role;
import com.aziz.library.domain.model.User;
import com.aziz.library.domain.port.in.ArticleUseCase;
import com.aziz.library.domain.port.out.ArticleRepositoryPort;
import com.aziz.library.domain.port.out.UserRepositoryPort;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService implements ArticleUseCase {

    private final ArticleRepositoryPort articleRepository;
    private final UserRepositoryPort userRepository;
    
    @Override
    @Transactional
    public Article createArticle(Article article, Long currentUserId) {
        log.info("Creating article by user: {}", currentUserId);
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Check permissions
        if (currentUser.getRole() == Role.VIEWER) {
            throw new UnauthorizedException("Viewers cannot create articles");
        }
        
        article.setAuthorId(currentUserId);
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        
        Article savedArticle = articleRepository.save(article);
        log.info("Article created with ID: {}", savedArticle.getId());
        
        return savedArticle;
    }
    
    @Override
    @Transactional
    public Article updateArticle(Long id, Article article, Long currentUserId) {
        log.info("Updating article {} by user: {}", id, currentUserId);
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found"));
        
        // Check permissions
        boolean canUpdate = false;
        
        if (currentUser.getRole() == Role.SUPER_ADMIN) {
            canUpdate = true;
        } else if (currentUser.getRole() == Role.EDITOR && 
                   existingArticle.getAuthorId().equals(currentUserId)) {
            canUpdate = true;
        } else if (currentUser.getRole() == Role.CONTRIBUTOR && 
                   existingArticle.getAuthorId().equals(currentUserId)) {
            canUpdate = true;
        }
        
        if (!canUpdate) {
            throw new UnauthorizedException("You don't have permission to update this article");
        }
        
        existingArticle.setTitle(article.getTitle());
        existingArticle.setContent(article.getContent());
        existingArticle.setPublic(article.isPublic());
        existingArticle.setUpdatedAt(LocalDateTime.now());
        
        Article updatedArticle = articleRepository.save(existingArticle);
        log.info("Article updated: {}", id);
        
        return updatedArticle;
    }
    
    @Override
    @Transactional
    public void deleteArticle(Long id, Long currentUserId) {
        log.info("Deleting article {} by user: {}", id, currentUserId);
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found"));
        
        // Check permissions
        boolean canDelete = false;
        
        if (currentUser.getRole() == Role.SUPER_ADMIN) {
            canDelete = true;
        } else if (currentUser.getRole() == Role.EDITOR && 
                   article.getAuthorId().equals(currentUserId)) {
            canDelete = true;
        }
        
        if (!canDelete) {
            throw new UnauthorizedException("You don't have permission to delete this article");
        }
        
        articleRepository.deleteById(id);
        log.info("Article deleted: {}", id);
    }
    
    @Override
    public Article getArticleById(Long id, Long currentUserId) {
        log.debug("Getting article {} for user: {}", id, currentUserId);
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ArticleNotFoundException("Article not found"));
        
        // Check if user can view this article
        if (currentUser.getRole() == Role.SUPER_ADMIN || 
            currentUser.getRole() == Role.EDITOR) {
            return article;
        }
        
        if (currentUser.getRole() == Role.CONTRIBUTOR && 
            article.getAuthorId().equals(currentUserId)) {
            return article;
        }
        
        if (currentUser.getRole() == Role.VIEWER && article.isPublic()) {
            return article;
        }
        
        throw new UnauthorizedException("You don't have permission to view this article");
    }
    
    @Override
    public List<Article> getAllArticles(Long currentUserId) {
        log.debug("Getting all articles for user: {}", currentUserId);
        
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        if (currentUser.getRole() == Role.SUPER_ADMIN || 
            currentUser.getRole() == Role.EDITOR) {
            return articleRepository.findAll();
        }
        
        if (currentUser.getRole() == Role.VIEWER) {
            return articleRepository.findAllPublic();
        }
        
        // Contributors can see all public articles + their own
        List<Article> publicArticles = articleRepository.findAllPublic();
        List<Article> myArticles = articleRepository.findByAuthorId(currentUserId);
        
        // Merge lists (avoiding duplicates)
        myArticles.stream()
            .filter(a -> !publicArticles.contains(a))
            .forEach(publicArticles::add);
        
        return publicArticles;
    }
    
    @Override
    public List<Article> getMyArticles(Long currentUserId) {
        log.debug("Getting my articles for user: {}", currentUserId);
        return articleRepository.findByAuthorId(currentUserId);
    }
}
