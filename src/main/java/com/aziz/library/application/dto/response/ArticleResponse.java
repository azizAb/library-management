package com.aziz.library.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Article information response")
public class ArticleResponse {

    @Schema(description = "Article ID", example = "1")
    private Long id;
    
    @Schema(description = "Article title", example = "Manual Book of Life")
    private String title;
    
    @Schema(description = "Article content")
    private String content;
    
    @Schema(description = "Author ID", example = "1")
    private Long authorId;
    
    @Schema(description = "Author username", example = "abdulaziz")
    private String authorUsername;
    
    @Schema(description = "Is article public", example = "true")
    private boolean isPublic;
    
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

}
