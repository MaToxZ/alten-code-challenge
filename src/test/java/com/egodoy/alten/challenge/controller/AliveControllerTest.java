package com.egodoy.alten.challenge.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
class AliveControllerTest {

    @InjectMocks
    private AliveController aliveController;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Test
    void isAlive() {
        ResponseEntity<String> response = aliveController.isAlive(httpServletRequest);

        Assertions.assertTrue(response.getBody().contains("Application is live..."));
    }
}