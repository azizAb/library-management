package com.aziz.library.domain.service;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BubbleSortService {

    public int[] sort(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        
        log.debug("Starting bubble sort for array: {}", Arrays.toString(array));
        
        int[] result = Arrays.copyOf(array, array.length);
        int n = result.length;
        boolean swapped;
        
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                if (result[j] > result[j + 1]) {
                    // Swap elements
                    int temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                    swapped = true;
                }
            }
            
            // If no swapping occurred, array is already sorted
            if (!swapped) {
                log.debug("Array sorted early at iteration {}", i);
                break;
            }
        }
        
        log.debug("Bubble sort completed. Result: {}", Arrays.toString(result));
        return result;
    }
    
    /**
     * Sorts an array in descending order using Bubble Sort
     */
    public int[] sortDescending(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        
        log.debug("Starting bubble sort descending for array: {}", Arrays.toString(array));
        
        int[] result = Arrays.copyOf(array, array.length);
        int n = result.length;
        boolean swapped;
        
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                if (result[j] < result[j + 1]) {
                    // Swap elements
                    int temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                    swapped = true;
                }
            }
            
            if (!swapped) {
                break;
            }
        }
        
        log.debug("Bubble sort descending completed. Result: {}", Arrays.toString(result));
        return result;
    }
    
    /**
     * Generic bubble sort for any comparable objects
     */
    public <T extends Comparable<T>> T[] sortGeneric(T[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        
        T[] result = Arrays.copyOf(array, array.length);
        int n = result.length;
        boolean swapped;
        
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            
            for (int j = 0; j < n - i - 1; j++) {
                if (result[j].compareTo(result[j + 1]) > 0) {
                    T temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                    swapped = true;
                }
            }
            
            if (!swapped) {
                break;
            }
        }
        
        return result;
    }

}
