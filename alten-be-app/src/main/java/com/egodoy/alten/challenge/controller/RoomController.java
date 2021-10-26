package com.egodoy.alten.challenge.controller;

import com.egodoy.alten.challenge.exception.AltenChallengeApiError;
import com.egodoy.alten.challenge.exception.AltenChallengeException;
import com.egodoy.alten.challenge.model.dto.RoomAvailabilityDto;
import com.egodoy.alten.challenge.service.RoomService;
import com.egodoy.alten.challenge.utils.PaginatedResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/room")
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService){
        this.roomService =  roomService;
    }

    @Operation( description = "Searching rooms availability.",
            summary = "Searches for rooms availability based in a date range")
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "OK", content = @Content(mediaType ="application/json",
                    schema = @Schema(implementation =  PaginatedResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid dates provided", content = @Content(mediaType ="application/json",
                    schema = @Schema(implementation =  AltenChallengeApiError.class)))})
    @GetMapping("/availability")
    public ResponseEntity<PaginatedResult<RoomAvailabilityDto>> getRoomAvailability(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate stayFrom,
                                                                                    @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate stayTo,
                                                                                    @RequestParam(required = false) Optional<Integer> pageNumber,
                                                                                    @RequestParam(required = false) Optional<Integer> pageSize) throws AltenChallengeException {
        try {
            return new ResponseEntity<>(roomService.findAvailabilityByDateRange(stayFrom,stayTo,
                    pageNumber,pageSize), HttpStatus.OK);
        } catch (AltenChallengeException e) {
            e.getApiError().setEndpoint("/room/availability");
            throw e;
        }
    }

}
