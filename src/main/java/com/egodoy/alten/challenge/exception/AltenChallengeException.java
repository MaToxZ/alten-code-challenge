package com.egodoy.alten.challenge.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Custom Exception class to wrap most of the exceptions thrown by different application layers
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AltenChallengeException extends Exception{
    AltenChallengeApiError apiError;

}
