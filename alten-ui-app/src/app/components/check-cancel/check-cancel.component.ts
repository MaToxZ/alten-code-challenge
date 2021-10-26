import { DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Booking } from 'src/app/model/booking';
import { ReservationRoomEvent } from 'src/app/model/reservationRoomEvent';
import { RoomAvailability } from 'src/app/model/roomAvailability';
import { ReservationService } from 'src/app/services/reservation.service';
import { RoomService } from 'src/app/services/room.service';
import { SnackBarService } from 'src/app/services/snack-bar.service';
import { BookingModalComponent } from '../booking-modal/booking-modal.component';

@Component({
  selector: 'app-check-cancel',
  templateUrl: './check-cancel.component.html',
  styleUrls: ['./check-cancel.component.scss']
})
export class CheckCancelComponent implements OnInit {

  searchReservationControl: FormControl = new FormControl();
  booking: Booking | undefined;
  roomAvailability: RoomAvailability | undefined;
  constructor(
    private reservationService: ReservationService,
    private snackBarService: SnackBarService,
    public modal: MatDialog,
    private datePipe: DatePipe,
    private roomService: RoomService
  ) { }

  ngOnInit(): void {
  }

  /**
   * This is the method invoked when user provide an reservation id and click the search
   * icon.
   * @param id 
   */
  searchReservation(id: any): void{
    this.reservationService.findReservation(id).subscribe((data) => {
      this.booking = data;
    },(errors) => {
      this.snackBarService.openSnackBar('error', errors.error.debugMessage,'close')
    })
  }

  /**
   * This method is used to manage the different types of event that a user can perform when
   * it's reviewing an existing reservation. Users are allowed to cancel room reservations
   * change stay dates for specific rooms.
   * @param event ReservationRoomEvent to update, cancel or check room availability for room
   */
  handleReservationChange(event: ReservationRoomEvent): void{
    if(event){
      switch (event.action) {
        case "UPDATE":
        case "CANCEL":{
          if(this.booking && this.booking.reservation && this.booking.reservation.id){
            if(event.reservationRoom && this.booking.rooms && this.booking.rooms.length > 0){
              this.booking.rooms = this.booking.rooms.map((reservationRoom) => {
                if(reservationRoom.id === event.reservationRoom.id){
                  reservationRoom.status = event.reservationRoom.status;
                  reservationRoom.stayFrom = event.reservationRoom.stayFrom;
                  reservationRoom.stayTo = event.reservationRoom.stayTo
                }
                return reservationRoom;
              })
            }
            this.reservationService.modify(this.booking).subscribe((data) => {
              if(data){
                const activeRooms = data.rooms?.filter((reservationRoom) => reservationRoom.status != 0);
                this.booking = {
                  reservation: data.reservation,
                  rooms: activeRooms
                }
                this.roomAvailability = undefined;
                this.snackBarService.openSnackBar('success','Reservation Modified Successfully','close')
              }   
            }, (errors) => {
              this.snackBarService.openSnackBar('error','There was an error trying to book your reservation: ' + errors.error.debugMessage,'close')
            })
          }
        }break;
        case "REQUEST_AVAILABILITY":{
          const bookingDialog = this.modal.open(BookingModalComponent, {
            width: '20%',
            data:{
              operation: "availability"
            }
          });
          bookingDialog.afterClosed().subscribe((result) => {
            if(result && result.operation === 'availability'){
              let fromString = this.datePipe.transform(result.from, 'yyyy-MM-dd');
              let toString = this.datePipe.transform(result.to, 'yyyy-MM-dd');
              this.roomService.findRoomsAvailability(fromString, toString).subscribe((response) => {
                if(response && response.data){
                  this.roomAvailability = response.data.find((roomAvailability) => roomAvailability.room.id == event.reservationRoom.room.id);
                  if(!this.roomAvailability){
                    this.snackBarService.openSnackBar('error', 'There are no available dates for the room within the date range selected', 'close');
                  }
                }  
                console.log("Service Response: ", response);
              })
            }
          });
        }break;
        default:
          break;
      }
    }
  }

  /**
   * this method is the reponsible of canceling the whole reservation (all its rooms) 
   * and it's triggered when user click on the 'Cancel Reservation' Button
   */
  cancelReservation(): void{
    if(this.booking && this.booking.reservation && this.booking.reservation.id){
      this.reservationService.cancel(this.booking.reservation?.id).subscribe((data) => {
        this.booking = undefined;
        this.snackBarService.openSnackBar('success',data,'close')
      }, (errors) => {
        this.snackBarService.openSnackBar('error','There was an error trying to book your reservation: ' + errors.error.debugMessage,'close')
      })
    }
  }

}
