package com.aziz.library.domain.service;

import com.aziz.library.domain.exception.*;
import com.aziz.library.domain.model.*;
import com.aziz.library.domain.port.out.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceTest {

    @Mock
    private ArticleRepositoryPort articleRepository;
    
    @Mock
    private UserRepositoryPort userRepository;
    
    @Mock
    private CacheServicePort cacheService;
    
    @InjectMocks
    private ArticleService articleService;
    
    @Test
    void testCreateArticle_AsContributor_ShouldCreateSuccessfully() {
        User contributor = User.builder()
                .id(1L)
                .role(Role.CONTRIBUTOR)
                .build();
        
        Article article = Article.builder()
                .title("Test Article")
                .content("Test Content")
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(contributor));
        when(articleRepository.save(any(Article.class))).thenAnswer(i -> {
            Article a = i.getArgument(0);
            a.setId(1L);
            return a;
        });
        
        Article result = articleService.createArticle(article, 1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getAuthorId());
        verify(articleRepository).save(any(Article.class));
    }
    
    @Test
    void testCreateArticle_AsViewer_ShouldThrowUnauthorizedException() {
        User viewer = User.builder()
                .id(1L)
                .role(Role.VIEWER)
                .build();
        
        Article article = Article.builder()
                .title("Test Article")
                .content("Test Content")
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));
        
        assertThrows(UnauthorizedException.class, () -> 
            articleService.createArticle(article, 1L)
        );
        
        verify(articleRepository, never()).save(any());
    }
    
    @Test
    void testUpdateArticle_AsOwner_ShouldUpdateSuccessfully() {
        User editor = User.builder()
                .id(1L)
                .role(Role.EDITOR)
                .build();
        
        Article existing = Article.builder()
                .id(1L)
                .title("Old Title")
                .content("Old Content")
                .authorId(1L)
                .build();
        
        Article updated = Article.builder()
                .title("New Title")
                .content("New Content")
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(editor));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(articleRepository.save(any())).thenReturn(existing);
        
        Article result = articleService.updateArticle(1L, updated, 1L);
        
        assertNotNull(result);
        verify(articleRepository).save(any());
        verify(cacheService).set(anyString(), any(), anyLong());
    }
    
    @Test
    void testUpdateArticle_AsNonOwner_ShouldThrowUnauthorizedException() {
        User editor = User.builder()
                .id(1L)
                .role(Role.EDITOR)
                .build();
        
        Article existing = Article.builder()
                .id(1L)
                .authorId(2L)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(editor));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(existing));
        
        assertThrows(UnauthorizedException.class, () -> 
            articleService.updateArticle(1L, new Article(), 1L)
        );
    }
    
    @Test
    void testDeleteArticle_AsEditor_ShouldDeleteSuccessfully() {
        User editor = User.builder()
                .id(1L)
                .role(Role.EDITOR)
                .build();
        
        Article article = Article.builder()
                .id(1L)
                .authorId(1L)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(editor));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        
        articleService.deleteArticle(1L, 1L);
        
        verify(articleRepository).deleteById(1L);
        verify(cacheService).delete(anyString());
    }
    
    @Test
    void testDeleteArticle_AsContributor_ShouldThrowUnauthorizedException() {
        User contributor = User.builder()
                .id(1L)
                .role(Role.CONTRIBUTOR)
                .build();
        
        Article article = Article.builder()
                .id(1L)
                .authorId(1L)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(contributor));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        
        assertThrows(UnauthorizedException.class, () -> 
            articleService.deleteArticle(1L, 1L)
        );
        
        verify(articleRepository, never()).deleteById(any());
    }
    
    @Test
    void testDeleteArticle_AsSuperAdmin_ShouldDeleteAnyArticle() {
        User superAdmin = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        Article article = Article.builder()
                .id(1L)
                .authorId(2L)
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(superAdmin));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        
        articleService.deleteArticle(1L, 1L);
        
        verify(articleRepository).deleteById(1L);
    }
    
    @Test
    void testGetArticleById_FromCache_ShouldReturnCached() {
        User user = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        Article cachedArticle = Article.builder()
                .id(1L)
                .title("Cached")
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cacheService.get(anyString())).thenReturn(Optional.of(cachedArticle));
        
        Article result = articleService.getArticleById(1L, 1L);
        
        assertNotNull(result);
        assertEquals("Cached", result.getTitle());
        verify(articleRepository, never()).findById(any());
    }
    
    @Test
    void testGetArticleById_NotInCache_ShouldQueryDBAndCache() {
        User user = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        Article article = Article.builder()
                .id(1L)
                .title("DB Article")
                .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cacheService.get(anyString())).thenReturn(Optional.empty());
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
        
        Article result = articleService.getArticleById(1L, 1L);
        
        assertNotNull(result);
        verify(articleRepository).findById(1L);
        verify(cacheService).set(anyString(), any(), anyLong());
    }
    
    @Test
    void testGetAllArticles_AsSuperAdmin_ShouldReturnAll() {
        User superAdmin = User.builder()
                .id(1L)
                .role(Role.SUPER_ADMIN)
                .build();
        
        List<Article> articles = Arrays.asList(
            Article.builder().id(1L).build(),
            Article.builder().id(2L).build()
        );
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(superAdmin));
        when(articleRepository.findAll()).thenReturn(articles);
        
        List<Article> result = articleService.getAllArticles(1L);
        
        assertEquals(2, result.size());
    }
    
    @Test
    void testGetAllArticles_AsViewer_ShouldReturnPublicOnly() {
        User viewer = User.builder()
                .id(1L)
                .role(Role.VIEWER)
                .build();
        
        List<Article> publicArticles = Arrays.asList(
            Article.builder().id(1L).isPublic(true).build()
        );
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(viewer));
        when(articleRepository.findAllPublic()).thenReturn(publicArticles);
        
        List<Article> result = articleService.getAllArticles(1L);
        
        assertEquals(1, result.size());
        verify(articleRepository).findAllPublic();
    }
    
    @Test
    void testGetMyArticles_ShouldReturnUserArticles() {
        List<Article> myArticles = Arrays.asList(
            Article.builder().id(1L).authorId(1L).build(),
            Article.builder().id(2L).authorId(1L).build()
        );
        
        when(articleRepository.findByAuthorId(1L)).thenReturn(myArticles);
        
        List<Article> result = articleService.getMyArticles(1L);
        
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> a.getAuthorId().equals(1L)));
    }

}
