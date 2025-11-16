package com.aziz.library.presentation.controller;

import com.aziz.library.application.dto.response.ApiResponse;
import com.aziz.library.application.dto.response.ArticleResponse;
import com.aziz.library.domain.model.Article;
import com.aziz.library.domain.port.in.ArticleUseCase;
import com.aziz.library.application.mapper.ArticleMapper;
import com.aziz.library.infrastructure.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

class ArticleControllerTest {

    private ArticleUseCase articleUseCase;
    private ArticleMapper articleMapper;
    private CustomUserDetailsService userDetailsService;
    private ArticleController articleController;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        articleUseCase = mock(ArticleUseCase.class);
        articleMapper = mock(ArticleMapper.class);
        userDetailsService = mock(CustomUserDetailsService.class);
        authentication = mock(Authentication.class);
        articleController = new ArticleController(articleUseCase, articleMapper, userDetailsService);
    }

    @Test
    void deleteArticle_shouldCallUseCaseAndReturnSuccessResponse() {
        Long articleId = 42L;
        Long userId = 100L;
        String username = "user1";

        when(authentication.getName()).thenReturn(username);
        when(userDetailsService.getUserIdByUsername(username)).thenReturn(userId);

        ResponseEntity<ApiResponse<Void>> response = articleController.deleteArticle(articleId, authentication);

        // Verify use case is called with correct arguments
        verify(articleUseCase, times(1)).deleteArticle(articleId, userId);

        // Verify response
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("Article deleted successfully", response.getBody().getMessage());
        assertNull(response.getBody().getData());
    }

    @Test
    void deleteArticle_shouldPropagateExceptionFromUseCase() {
        Long articleId = 99L;
        Long userId = 200L;
        String username = "user2";

        when(authentication.getName()).thenReturn(username);
        when(userDetailsService.getUserIdByUsername(username)).thenReturn(userId);
        doThrow(new RuntimeException("Not allowed")).when(articleUseCase).deleteArticle(articleId, userId);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                articleController.deleteArticle(articleId, authentication)
        );

        assertEquals("Not allowed", thrown.getMessage());
        verify(articleUseCase, times(1)).deleteArticle(articleId, userId);
    }
    @Test
    void getMyArticles_shouldReturnListOfArticleResponses() {
        Long userId = 123L;
        String username = "testuser";
        Article article1 = mock(Article.class);
        Article article2 = mock(Article.class);
        ArticleResponse response1 = mock(ArticleResponse.class);
        ArticleResponse response2 = mock(ArticleResponse.class);

        when(authentication.getName()).thenReturn(username);
        when(userDetailsService.getUserIdByUsername(username)).thenReturn(userId);
        when(articleUseCase.getMyArticles(userId)).thenReturn(List.of(article1, article2));
        when(articleMapper.toResponse(article1)).thenReturn(response1);
        when(articleMapper.toResponse(article2)).thenReturn(response2);

        ResponseEntity<ApiResponse<List<ArticleResponse>>> response = articleController.getMyArticles(authentication);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("My articles retrieved successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(2, response.getBody().getData().size());
        assertTrue(response.getBody().getData().contains(response1));
        assertTrue(response.getBody().getData().contains(response2));

        verify(articleUseCase, times(1)).getMyArticles(userId);
        verify(articleMapper, times(1)).toResponse(article1);
        verify(articleMapper, times(1)).toResponse(article2);
    }

    @Test
    void getMyArticles_shouldReturnEmptyListWhenNoArticles() {
        Long userId = 456L;
        String username = "emptyuser";

        when(authentication.getName()).thenReturn(username);
        when(userDetailsService.getUserIdByUsername(username)).thenReturn(userId);
        when(articleUseCase.getMyArticles(userId)).thenReturn(List.of());

        ResponseEntity<ApiResponse<List<ArticleResponse>>> response = articleController.getMyArticles(authentication);

        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("My articles retrieved successfully", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertTrue(response.getBody().getData().isEmpty());

        verify(articleUseCase, times(1)).getMyArticles(userId);
        verify(articleMapper, never()).toResponse(any());
    }

    @Test
    void getMyArticles_shouldPropagateExceptionFromUseCase() {
        Long userId = 789L;
        String username = "erroruser";

        when(authentication.getName()).thenReturn(username);
        when(userDetailsService.getUserIdByUsername(username)).thenReturn(userId);
        when(articleUseCase.getMyArticles(userId)).thenThrow(new RuntimeException("DB error"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                articleController.getMyArticles(authentication)
        );

        assertEquals("DB error", thrown.getMessage());
        verify(articleUseCase, times(1)).getMyArticles(userId);
    }
}
