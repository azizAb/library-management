package com.aziz.library.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Bubble sort result response")
public class BubbleSortResponse {

    @Schema(description = "Original array", example = "[5, 2, 8, 1, 9]")
    private int[] originalArray;
    
    @Schema(description = "Sorted array", example = "[1, 2, 5, 8, 9]")
    private int[] sortedArray;
    
    @Schema(description = "Sort order used", example = "ASC")
    private String order;
    
    @Schema(description = "Number of elements sorted", example = "5")
    private int elementCount;
    
    @Schema(description = "Time taken in milliseconds", example = "2")
    private long executionTimeMs;

}
