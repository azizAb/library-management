package com.aziz.library.application.mapper;

import com.aziz.library.application.dto.request.ArticleRequest;
import com.aziz.library.application.dto.response.ArticleResponse;
import com.aziz.library.domain.model.Article;
import com.aziz.library.domain.model.User;
import com.aziz.library.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleMapperTest {

    private ArticleMapper articleMapper;
    private UserRepositoryPort userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepositoryPort.class);
        articleMapper = Mappers.getMapper(ArticleMapper.class);
        // Inject mock UserRepositoryPort into the mapper
        articleMapper.userRepository = userRepository;
    }

    @Test
    void toResponse_shouldMapFieldsCorrectly_andResolveAuthorUsername() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Title");
        article.setContent("Test Content");
        article.setAuthorId(42L);
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        article.setPublic(true);

        User user = new User();
        user.setId(42L);
        user.setUsername("john_doe");

        when(userRepository.findById(42L)).thenReturn(Optional.of(user));

        ArticleResponse response = articleMapper.toResponse(article);

        assertEquals("Test Title", response.getTitle());
        assertEquals("Test Content", response.getContent());
        assertEquals("john_doe", response.getAuthorUsername());
        assertTrue(response.isPublic());
    }

    @Test
    void toResponse_shouldReturnUnknownWhenUserNotFound() {
        Article article = new Article();
        article.setAuthorId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ArticleResponse response = articleMapper.toResponse(article);

        assertEquals("Unknown", response.getAuthorUsername());
    }

    @Test
    void toDomain_shouldMapFieldsCorrectly() {
        ArticleRequest request = new ArticleRequest();
        request.setTitle("Article Title");
        request.setContent("Article Content");
        request.setPublic(true);

        Article article = articleMapper.toDomain(request);

        assertEquals("Article Title", article.getTitle());
        assertEquals("Article Content", article.getContent());
        assertTrue(article.isPublic());
        assertNull(article.getId());
        assertNull(article.getAuthorId());
        assertNull(article.getCreatedAt());
        assertNull(article.getUpdatedAt());
    }

    @Test
    void updateDomainFromRequest_shouldUpdateFieldsExceptIgnored() {
        ArticleRequest request = new ArticleRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");
        request.setPublic(false);

        Article article = new Article();
        article.setId(5L);
        article.setAuthorId(10L);
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        article.setTitle("Old Title");
        article.setContent("Old Content");
        article.setPublic(true);

        articleMapper.updateDomainFromRequest(request, article);

        assertEquals("Updated Title", article.getTitle());
        assertEquals("Updated Content", article.getContent());
        assertFalse(article.isPublic());
        // Ignored fields should remain unchanged
        assertEquals(5L, article.getId());
        assertEquals(10L, article.getAuthorId());
        assertNotNull(article.getCreatedAt());
    }
}