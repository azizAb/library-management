package com.aziz.library.presentation.controller;

import java.util.Arrays;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aziz.library.application.dto.request.BubbleSortRequest;
import com.aziz.library.application.dto.response.ApiResponse;
import com.aziz.library.application.dto.response.BubbleSortResponse;
import com.aziz.library.domain.service.BubbleSortService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/bubble-sort")
@RequiredArgsConstructor
@Tag(name = "Bubble Sort", description = "Bubble sort algorithm implementation (public access)")
public class BubbleSortController {

private final BubbleSortService bubbleSortService;
    
    @PostMapping
    @Operation(
        summary = "Sort array using Bubble Sort", 
        description = "Sorts an array of integers using the Bubble Sort algorithm. Supports both ascending (ASC) and descending (DESC) order. No authentication required."
    )
    public ResponseEntity<ApiResponse<BubbleSortResponse>> sortArray(
            @Valid @RequestBody BubbleSortRequest request) {
        
        log.info("Bubble sort request received. Array length: {}, Order: {}", 
                request.getArray().length, request.getOrder());
        
        // Start timer
        long startTime = System.currentTimeMillis();
        
        // Copy original array
        int[] originalArray = Arrays.copyOf(request.getArray(), request.getArray().length);
        
        // Sort array
        int[] sortedArray;
        if ("DESC".equalsIgnoreCase(request.getOrder())) {
            sortedArray = bubbleSortService.sortDescending(request.getArray());
        } else {
            sortedArray = bubbleSortService.sort(request.getArray());
        }
        
        // Calculate execution time
        long executionTime = System.currentTimeMillis() - startTime;
        
        // Build response
        BubbleSortResponse response = BubbleSortResponse.builder()
                .originalArray(originalArray)
                .sortedArray(sortedArray)
                .order(request.getOrder().toUpperCase())
                .elementCount(sortedArray.length)
                .executionTimeMs(executionTime)
                .build();
        
        log.info("Bubble sort completed in {}ms. Array sorted from {} to {}", 
                executionTime, Arrays.toString(originalArray), Arrays.toString(sortedArray));
        
        return ResponseEntity.ok(
            ApiResponse.success("Array sorted successfully using Bubble Sort algorithm", response)
        );
    }
    
    @GetMapping("/demo")
    @Operation(
        summary = "Demo bubble sort", 
        description = "Demonstrates bubble sort with a sample array [5, 2, 8, 1, 9, 3]. No authentication required."
    )
    public ResponseEntity<ApiResponse<BubbleSortResponse>> demoSort() {
        
        log.info("Demo bubble sort request");
        
        int[] demoArray = {5, 2, 8, 1, 9, 3};
        int[] originalArray = Arrays.copyOf(demoArray, demoArray.length);
        
        long startTime = System.currentTimeMillis();
        int[] sortedArray = bubbleSortService.sort(demoArray);
        long executionTime = System.currentTimeMillis() - startTime;
        
        BubbleSortResponse response = BubbleSortResponse.builder()
                .originalArray(originalArray)
                .sortedArray(sortedArray)
                .order("ASC")
                .elementCount(sortedArray.length)
                .executionTimeMs(executionTime)
                .build();
        
        return ResponseEntity.ok(
            ApiResponse.success("Demo array sorted successfully", response)
        );
    }
    
    @PostMapping("/analyze")
    @Operation(
        summary = "Analyze sorting performance",
        description = "Returns detailed performance analysis of the bubble sort algorithm. No authentication required."
    )
    public ResponseEntity<ApiResponse<Object>> analyzePerformance(
            @Valid @RequestBody BubbleSortRequest request) {
        
        log.info("Performance analysis request for array of size: {}", request.getArray().length);
        
        int[] originalArray = Arrays.copyOf(request.getArray(), request.getArray().length);
        
        // Measure sorting time
        long startTime = System.nanoTime();
        int[] sortedArray = bubbleSortService.sort(request.getArray());
        long endTime = System.nanoTime();
        
        long executionTimeNs = endTime - startTime;
        double executionTimeMs = executionTimeNs / 1_000_000.0;
        
        // Calculate performance metrics
        int n = request.getArray().length;
        int worstCaseComparisons = (n * (n - 1)) / 2;
        int bestCaseComparisons = n - 1;
        
        // Build analysis response
        var analysis = new java.util.HashMap<String, Object>();
        analysis.put("originalArray", originalArray);
        analysis.put("sortedArray", sortedArray);
        analysis.put("arraySize", n);
        analysis.put("executionTimeMs", String.format("%.4f", executionTimeMs));
        analysis.put("executionTimeNs", executionTimeNs);
        analysis.put("algorithm", "Bubble Sort");
        analysis.put("timeComplexity", "O(n²) worst/average, O(n) best");
        analysis.put("spaceComplexity", "O(1)");
        analysis.put("worstCaseComparisons", worstCaseComparisons);
        analysis.put("bestCaseComparisons", bestCaseComparisons);
        analysis.put("stable", true);
        analysis.put("inPlace", true);
        
        return ResponseEntity.ok(
            ApiResponse.success("Performance analysis completed", analysis)
        );
    }

}
