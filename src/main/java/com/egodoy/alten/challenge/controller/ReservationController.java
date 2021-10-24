package com.egodoy.alten.challenge.controller;

import com.egodoy.alten.challenge.exception.AltenChallengeApiError;
import com.egodoy.alten.challenge.exception.AltenChallengeException;
import com.egodoy.alten.challenge.model.dto.BookingDTO;
import com.egodoy.alten.challenge.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService){
        this.reservationService =  reservationService;
    }

    @Operation( description = "Booking a reservation.",
            summary = "Booking desired rooms through a reservation.")
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "OK", content = @Content(mediaType ="application/json",
                    schema = @Schema(implementation =  BookingDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Object / Data provided", content = @Content(mediaType ="application/json",
                    schema = @Schema(implementation =  AltenChallengeApiError.class)))})
    @PostMapping("/book")
    public ResponseEntity<BookingDTO> book(@RequestBody BookingDTO reservation) throws AltenChallengeException {
        try {
            return new ResponseEntity<>(reservationService.book(reservation), HttpStatus.OK);
        } catch (AltenChallengeException e) {
            e.getApiError().setEndpoint("/reservation/book");
            throw e;
        }
    }

    @Operation( description = "Modifying a reservation.",
            summary = "Modifies an existing reservation. Modifications supported are: Room cancellation, Room Stay Dates.")
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "OK", content = @Content(mediaType ="application/json",
                    schema = @Schema(implementation =  BookingDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Object / Data provided", content = @Content(mediaType ="application/json",
                    schema = @Schema(implementation =  AltenChallengeApiError.class)))})
    @PatchMapping("/modify")
    public ResponseEntity<BookingDTO> modifyReservation(@RequestBody BookingDTO reservation) throws AltenChallengeException {
        try {
            return new ResponseEntity<>(reservationService.modifyReservation(reservation), HttpStatus.OK);
        } catch (AltenChallengeException e) {
            e.getApiError().setEndpoint("/reservation/modify");
            throw e;
        }
    }

    @Operation( description = "Cancelling a reservation.",
            summary = "Cancel an existing and active reservation")
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "Reservation Cancelled successfully", content = @Content(mediaType ="text/plan",
                    schema = @Schema(implementation =  String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid reservationId provided", content = @Content(mediaType ="application/json",
                    schema = @Schema(implementation =  AltenChallengeApiError.class)))})
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<String> cancelReservation(@PathVariable UUID id) throws AltenChallengeException {
        try {
            reservationService.cancelReservation(id);
            return new ResponseEntity<>("Reservation Cancelled successfully", HttpStatus.OK);
        } catch (AltenChallengeException e) {
            e.getApiError().setEndpoint("/reservation/cancel");
            throw e;
        }
    }

    @Operation( description = "Retrieving a reservation.",
            summary = "Retrieves an existing and active reservation")
    @ApiResponses( value = {
            @ApiResponse( responseCode = "200", description = "OK", content = @Content(mediaType ="application/json",
                    schema = @Schema(implementation =  BookingDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid reservationId provided", content = @Content(mediaType ="application/json",
                    schema = @Schema(implementation =  AltenChallengeApiError.class)))})
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getReservation(@PathVariable UUID id) throws AltenChallengeException {
        try {
            BookingDTO reservation = reservationService.getReservation(id);
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        } catch (AltenChallengeException e) {
            e.getApiError().setEndpoint("/reservation/".concat(id.toString()));
            throw e;
        }
    }
}
