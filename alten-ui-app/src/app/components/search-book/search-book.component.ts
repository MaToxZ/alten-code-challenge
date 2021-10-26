import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { PaginatedResult } from 'src/app/model/paginatedResult';
import { RoomAvailability } from 'src/app/model/roomAvailability';
import { RoomService } from 'src/app/services/room.service';

import * as _moment from 'moment';
import { Moment } from 'moment';
import { ReservationRoomEvent } from 'src/app/model/reservationRoomEvent';
import { Booking } from 'src/app/model/booking';
import { ReservationService } from 'src/app/services/reservation.service';
import { SnackBarService } from 'src/app/services/snack-bar.service';
import { MatDialog } from '@angular/material/dialog';
import { BookingModalComponent } from '../booking-modal/booking-modal.component';
import { Reservation } from 'src/app/model/reservation';

const moment = _moment;

@Component({
  selector: 'app-search-book',
  templateUrl: './search-book.component.html',
  styleUrls: ['./search-book.component.scss']
})
export class SearchBookComponent implements OnInit {

  availabilityRange = new FormGroup({
    start: new FormControl(),
    end: new FormControl()
  });

  availabilityResponse: PaginatedResult<RoomAvailability> | undefined;
  roomsAvailables: Array<RoomAvailability> | undefined;
  booking: Booking | undefined;

  /**
   * This method is responsible of disabling certain dates from the calendar GUI. 
   * In this case is to not allow users to select from past dates or future dates 
   * greater than 30 days
   * @param m Moment Date
   * @returns 
   */
  myDateFilter = (m: Moment | null): boolean => {
    const dateNum = (m || moment());
    return dateNum > moment() && dateNum <= moment().add(30,'days');
  } 

  constructor(
    private roomService: RoomService,
    private reservationService: ReservationService,
    private datePipe: DatePipe,
    private snackBarService: SnackBarService,
    public modal: MatDialog
  ) { }

  /**
   * Angular method to initialize the component. In this method we add a
   * subcription to the "end" form control, so that everytime it changes
   * and its value is different to null, we request the BE to return the
   * available rooms and dates for the given date range
   */
  ngOnInit(): void {
    this.availabilityRange.get('end')?.valueChanges.subscribe((data) => {
      if(data != null){
        let from = this.datePipe.transform(this.availabilityRange.get('start')?.value, 'yyyy-MM-dd');
        let to = this.datePipe.transform(data, 'yyyy-MM-dd');
        console.log("from: ", from);
        console.log("to: ", to);
        if(from && to){
          this.roomService.findRoomsAvailability(from, to).subscribe((response) => {
            this.availabilityResponse = response;
            this.roomsAvailables = response.data;
            console.log("Service Response: ", response);
          }, (errors) => {
            this.snackBarService.openSnackBar('error', errors.error.message + ': ' + errors.error.debugMessage,'close')
          })
        }
      }
    })
  }

  /**
   * This method is used to manage the different types of event that a user can perform when
   * it's creating reservation. Users are allowed to add and remove previously added rooms
   * @param event ReservationRoomEvent to add or remove a room for a new reservation
   */
  handleReservationChange(change: ReservationRoomEvent){
      if(change){
        switch (change.action) {
          case "ADD":{
            if(this.booking && this.booking.rooms){
              this.booking.rooms.push(change.reservationRoom)
            }else{
              this.booking = {
                rooms: [change.reservationRoom]
              }
            }
          }break;
          case "REMOVE":{
            if(this.booking && this.booking.rooms){
              const index = this.booking.rooms.findIndex(reservation => reservation.room.id == change.reservationRoom.room.id);
              this.booking.rooms.splice(index,1);
            }
          }break;
          default:
            break;
        }
      }
  }

  /**
   * this method is triggered when the "Book Reservation" button is clicked. It opens a modal
   * that request the user to fill in the Visitor Information, once this info is filled in
   * then it hit the BE to book the new Reservation
   */
  bookReservation(): void{
    if(this.booking && this.booking.rooms && this.booking.rooms.length > 0){
      const bookingDialog = this.modal.open(BookingModalComponent, {
        width: '20%',
        data:{
          operation: "visitorInfo"
        }
      });
      bookingDialog.afterClosed().subscribe((result) => {
        debugger;
        if(result && result.operation === 'visitorInfo'){
          this.booking!.reservation = {
            visitorName: result.name,
            visitorEmail: result.email, 
            visitorPhone: result.phone,
            status: 1
          }
          this.reservationService.book(this.booking).subscribe((data)=>{
            if(data && data.reservation?.id){
              this.resetSearch();
              this.snackBarService.openSnackBar('success','You reservation has beed successfully booked under the id: ' + data.reservation.id,'close')
            }
          },(errors) => {
            this.snackBarService.openSnackBar('error','There was an error trying to book your reservation: ' + errors.error.debugMessage,'close')
          })
        }
      });
    }
  }

  /**
   * This method is used to reset the form after a reservation is booked
   */
  resetSearch(): void{
    this.availabilityRange.reset()
    this.availabilityResponse = undefined;
    this.roomsAvailables = undefined;
    this.booking = undefined;
  }

}
