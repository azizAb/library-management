package com.aziz.library.presentation.controller;

import com.aziz.library.application.dto.request.ArticleRequest;
import com.aziz.library.application.dto.response.ApiResponse;
import com.aziz.library.application.dto.response.ArticleResponse;
import com.aziz.library.application.mapper.ArticleMapper;
import com.aziz.library.domain.model.Article;
import com.aziz.library.domain.port.in.ArticleUseCase;
import com.aziz.library.infrastructure.security.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Articles", description = "Article management endpoints")
public class ArticleController {

    private final ArticleUseCase articleUseCase;
    private final ArticleMapper articleMapper;
    private final CustomUserDetailsService userDetailsService;
    
    @PostMapping
    @Operation(summary = "Create article", description = "Create a new article (requires CONTRIBUTOR, EDITOR, or SUPER_ADMIN role)")
    public ResponseEntity<ApiResponse<ArticleResponse>> createArticle(
            @Valid @RequestBody ArticleRequest request,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.info("Create article request by user: {}", currentUserId);
        
        Article article = articleMapper.toDomain(request);
        Article createdArticle = articleUseCase.createArticle(article, currentUserId);
        ArticleResponse response = articleMapper.toResponse(createdArticle);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Article created successfully", response));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update article", description = "Update an existing article")
    public ResponseEntity<ApiResponse<ArticleResponse>> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequest request,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.info("Update article {} by user: {}", id, currentUserId);
        
        Article article = articleMapper.toDomain(request);
        Article updatedArticle = articleUseCase.updateArticle(id, article, currentUserId);
        ArticleResponse response = articleMapper.toResponse(updatedArticle);
        
        return ResponseEntity.ok(ApiResponse.success("Article updated successfully", response));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete article", description = "Delete an article (EDITOR can delete own, SUPER_ADMIN can delete any)")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.info("Delete article {} by user: {}", id, currentUserId);
        
        articleUseCase.deleteArticle(id, currentUserId);
        
        return ResponseEntity.ok(ApiResponse.success("Article deleted successfully", null));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get article by ID", description = "Retrieve a specific article by its ID")
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticleById(
            @PathVariable Long id,
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.debug("Get article {} by user: {}", id, currentUserId);
        
        Article article = articleUseCase.getArticleById(id, currentUserId);
        ArticleResponse response = articleMapper.toResponse(article);
        
        return ResponseEntity.ok(ApiResponse.success("Article retrieved successfully", response));
    }
    
    @GetMapping
    @Operation(summary = "Get all articles", description = "Get all articles based on user's role and permissions")
    public ResponseEntity<ApiResponse<List<ArticleResponse>>> getAllArticles(
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.debug("Get all articles by user: {}", currentUserId);
        
        List<Article> articles = articleUseCase.getAllArticles(currentUserId);
        List<ArticleResponse> responses = articles.stream()
                .map(articleMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("Articles retrieved successfully", responses));
    }
    
    @GetMapping("/my-articles")
    @Operation(summary = "Get my articles", description = "Get all articles created by the current user")
    public ResponseEntity<ApiResponse<List<ArticleResponse>>> getMyArticles(
            Authentication authentication) {
        
        Long currentUserId = getCurrentUserId(authentication);
        log.debug("Get my articles for user: {}", currentUserId);
        
        List<Article> articles = articleUseCase.getMyArticles(currentUserId);
        List<ArticleResponse> responses = articles.stream()
                .map(articleMapper::toResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success("My articles retrieved successfully", responses));
    }
    
    private Long getCurrentUserId(Authentication authentication) {
        String username = authentication.getName();
        return userDetailsService.getUserIdByUsername(username);
    }

}
