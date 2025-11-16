package com.aziz.library.infrastructure.adapter.persistence;

import com.aziz.library.domain.model.Article;
import com.aziz.library.infrastructure.adapter.persistence.entity.ArticleEntity;
import com.aziz.library.infrastructure.adapter.persistence.repository.ArticleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ArticleRepositoryAdapterTest {

    private ArticleRepository articleRepository;
    private ArticleRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        articleRepository = mock(ArticleRepository.class);
        adapter = new ArticleRepositoryAdapter(articleRepository);
    }

    private ArticleEntity sampleEntity() {
        return ArticleEntity.builder()
                .id(1L)
                .title("Title")
                .content("Content")
                .authorId(2L)
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Article sampleDomain() {
        return Article.builder()
                .id(1L)
                .title("Title")
                .content("Content")
                .authorId(2L)
                .isPublic(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void save_shouldConvertAndDelegateToRepository() {
        Article article = sampleDomain();
        ArticleEntity entity = sampleEntity();

        when(articleRepository.save(any(ArticleEntity.class))).thenReturn(entity);

        Article result = adapter.save(article);

        ArgumentCaptor<ArticleEntity> captor = ArgumentCaptor.forClass(ArticleEntity.class);
        verify(articleRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo(article.getTitle());
        assertThat(result.getId()).isEqualTo(entity.getId());
        assertThat(result.getTitle()).isEqualTo(entity.getTitle());
    }

    @Test
    void findById_shouldReturnMappedDomainObject() {
        ArticleEntity entity = sampleEntity();
        when(articleRepository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<Article> result = adapter.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(entity.getId());
        assertThat(result.get().getTitle()).isEqualTo(entity.getTitle());
    }

    @Test
    void findById_shouldReturnEmptyIfNotFound() {
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Article> result = adapter.findById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_shouldReturnMappedList() {
        ArticleEntity entity1 = sampleEntity();
        ArticleEntity entity2 = ArticleEntity.builder()
                .id(2L)
                .title("Another")
                .content(entity1.getContent())
                .authorId(entity1.getAuthorId())
                .isPublic(entity1.isPublic())
                .createdAt(entity1.getCreatedAt())
                .updatedAt(entity1.getUpdatedAt())
                .build();
        when(articleRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));

        List<Article> result = adapter.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(entity1.getId());
        assertThat(result.get(1).getId()).isEqualTo(entity2.getId());
    }

    @Test
    void findByAuthorId_shouldReturnMappedList() {
        ArticleEntity entity = sampleEntity();
        when(articleRepository.findByAuthorId(2L)).thenReturn(List.of(entity));

        List<Article> result = adapter.findByAuthorId(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthorId()).isEqualTo(2L);
    }

    @Test
    void findAllPublic_shouldReturnMappedList() {
        ArticleEntity entity = sampleEntity();
        when(articleRepository.findByIsPublicTrue()).thenReturn(List.of(entity));

        List<Article> result = adapter.findAllPublic();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isPublic()).isTrue();
    }

    @Test
    void deleteById_shouldDelegateToRepository() {
        adapter.deleteById(1L);
        verify(articleRepository).deleteById(1L);
    }
}