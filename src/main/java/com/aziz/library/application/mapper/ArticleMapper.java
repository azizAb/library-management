package com.aziz.library.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.aziz.library.application.dto.request.ArticleRequest;
import com.aziz.library.application.dto.response.ArticleResponse;
import com.aziz.library.domain.model.Article;
import com.aziz.library.domain.port.out.UserRepositoryPort;

@Mapper(componentModel = "spring")
public abstract class ArticleMapper {

    @Autowired
    protected UserRepositoryPort userRepository;
    
    @Mapping(target = "authorUsername", expression = "java(getAuthorUsername(article.getAuthorId()))")
    public abstract ArticleResponse toResponse(Article article);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public abstract Article toDomain(ArticleRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract void updateDomainFromRequest(ArticleRequest request, @MappingTarget Article article);
    
    protected String getAuthorUsername(Long authorId) {
        return userRepository.findById(authorId)
                .map(user -> user.getUsername())
                .orElse("Unknown");
    }

}
