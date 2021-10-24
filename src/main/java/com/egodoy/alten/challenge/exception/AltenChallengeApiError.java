package com.egodoy.alten.challenge.exception;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

/**
 * Custom API Error object that is returned to the user when something goes wrong.
 * This object contains the related information about the issue / error / exception.
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AltenChallengeApiError {
    private HttpStatus status;

    private String message;

    private String endpoint;

    private String debugMessage;
}
