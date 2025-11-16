package com.aziz.library.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Bubble sort request payload")
public class BubbleSortRequest {

    @NotNull(message = "Array is required")
    @Size(min = 1, message = "Array must contain at least one element")
    @Schema(description = "Array of integers to sort", example = "[5, 2, 8, 1, 9]")
    private int[] array;
    
    @Schema(description = "Sort order (ASC or DESC)", example = "ASC", defaultValue = "ASC")
    private String order = "ASC";

}
