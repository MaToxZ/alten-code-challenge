import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { RoomAvailability } from 'src/app/model/roomAvailability';

import * as _moment from 'moment';
import { Moment } from 'moment';
import { DatePipe } from '@angular/common';
import { Room } from 'src/app/model/room';
import { DateRange } from '@angular/material/datepicker';
import { ReservationRoom } from 'src/app/model/reservationRoom';
import { ReservationService } from 'src/app/services/reservation.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { iif } from 'rxjs';
import { Booking } from 'src/app/model/booking';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { ReservationRoomEvent } from 'src/app/model/reservationRoomEvent';
import { SnackBarService } from 'src/app/services/snack-bar.service';

const moment = _moment;

@Component({
  selector: 'app-room-availability-card',
  templateUrl: './room-availability-card.component.html',
  styleUrls: ['./room-availability-card.component.scss']
})
export class RoomAvailabilityCardComponent implements OnInit {

  @Input() roomAvailability: RoomAvailability | undefined;
  @Input() reservationRoom: ReservationRoom | undefined;
  @Input() reservationId: string | undefined;
  room: Room | undefined | null;

  imageSrc: string = ""

  daysSelected: any[] = [];

  sampleRange: DateRange<Moment> | undefined;

  datesBetweenRangeSelected: string[] = [];

  overlapsDateStringList: string = "";

  @Output() reservationChange: EventEmitter<ReservationRoomEvent> = new EventEmitter<ReservationRoomEvent>();

  reservationRange = new FormGroup({
    start: new FormControl('', Validators.required),
    end: new FormControl('', Validators.required)
  });

  constructor(
    private datePipe: DatePipe,
  ) { }

  ngOnInit(): void {
    
  }

  /**
   * This method is responsible of disabling certain dates from the calendar GUI. 
   * In this case depending on the provided object, It's going to disabled those
   * dates that are not available for the room 
   * greater than 30 days
   * @param m Moment Date
   * @returns 
   */
  myDateFilter = (m: Moment | null): boolean => {
    const dateNum = (m || moment());
    if(this.roomAvailability){
      return this.roomAvailability.availableDates.includes(dateNum.format('yyyy-MM-DD')) ? true : false;
    }else if(this.reservationRoom){
      return false;
    }
    return true;
  } 

  /**
   * Angular method to intercept the new changes when a change is detected from an input
   * @param changes Input changes
   */
  ngOnChanges(changes: SimpleChanges){
    if(changes.roomAvailability?.currentValue != null){
      this.room = this.roomAvailability?.room;
    }
    
    if(changes.reservationRoom?.currentValue != null){
      this.room = this.reservationRoom?.room;
      this.sampleRange = new DateRange(moment(this.reservationRoom?.stayFrom), moment(this.reservationRoom?.stayTo));
    }

    
    if(this.room?.roomType){
      switch (this.room?.roomType.id) {
        case 1:
          this.imageSrc = "../../../assets/images/junior-room.png";
        break;
        case 2:
          this.imageSrc = "../../../assets/images/senior-room.jpeg";
        break;
        case 3:
          this.imageSrc = "../../../assets/images/executive-room.jpeg";
        break;
        case 4:
          this.imageSrc = "../../../assets/images/family-room.jpeg";
        break;
        case 5:
          this.imageSrc = "../../../assets/images/presidential-room.jpeg";
        break;
        default:
          break;
      }
    }
    
  }

  /**
   * This methods emit an output event to its parent to notify it that there is
   * a room that was cancelled, hence the reservation needs to be modified.
   */
  cancelReservation(): void {
    if(this.reservationRoom && this.reservationId && this.room){
      const reservationRoomEvent: ReservationRoomEvent = {
        action: 'CANCEL',
        reservationRoom: {
          id: this.reservationRoom.id,
          room: this.room,
          stayFrom: this.reservationRoom.stayFrom,
          stayTo: this.reservationRoom.stayTo,
          status: 0
        }
      }
      this.reservationChange.emit(reservationRoomEvent);
    }
    
  }

  /**
   * When users decided to modify the stay dates of an existing room in a reservation
   * we need to search for the current available dates for that room. This methods
   * emit and output event to notify its parent that needs to pop up the modal to search
   * for the date range the user is instered to change the stay dates for the room
   */
  searchRoomAvailability(): void {
    if(this.reservationRoom?.room?.id && this.room){
      const reservationRoomEvent: ReservationRoomEvent = {
        action: 'REQUEST_AVAILABILITY',
        reservationRoom: {
          id: this.reservationRoom.id,
          room: this.room,
          stayFrom: this.reservationRoom.stayFrom,
          stayTo: this.reservationRoom.stayTo,
          status: 0
        }
      }
      this.reservationChange.emit(reservationRoomEvent);
    }
  }

  /**
   * This methods emit an output event to its parent notifiying it that the stay dates have
   * changed and it needs to go and modify the reservation.
   */
  modifyReservation(): void{
    if(this.reservationRoom && this.reservationId && this.room){
      let from = this.datePipe.transform(this.reservationRange.get('start')?.value,'yyyy-MM-dd');
      let to = this.datePipe.transform(this.reservationRange.get('end')?.value,'yyyy-MM-dd');
      if(from && to){
        const reservationRoomEvent: ReservationRoomEvent = {
          action: 'UPDATE',
          reservationRoom: {
            id: this.reservationRoom.id,
            room: this.room,
            stayFrom: from,
            stayTo: to,
            status: 1
          }
        }
        this.reservationChange.emit(reservationRoomEvent);
      }
    }
  }

  /**
   * This method is triggered everytime that a value is assigned to the "end" edge of 
   * the date range. It validates based on the inputs it receives that if the selected
   * period is not overlapping unavaiable or allocated dates from other reservations
   * to that room.
   * @param reservationRange FormGroup containing both start and end for the date range
   */
  dateRangeChange(reservationRange: FormGroup){
    let start = reservationRange.get('start')?.value;
    let end = reservationRange.get('end')?.value;
    if(start && end){
        this.datesBetweenRangeSelected = [];
        while(moment(start) <= moment(end)){
          this.datesBetweenRangeSelected.push(start.format("YYYY-MM-DD"));
          start = moment(start).add(1, 'days');
        }
        this.overlapsDateStringList = this.datesBetweenRangeSelected.filter((date) => !this.roomAvailability?.availableDates.includes(date)).reduce((pre, curr, index) => (index ? pre + ', ' : '') + curr, '');
        if(this.overlapsDateStringList && this.overlapsDateStringList.trim() != ''){
          console.log("There are overlap dates in your range selection: " + this.overlapsDateStringList)
          this.reservationRange.get('start')?.setErrors({overlaps: true});
          this.reservationRange.get('end')?.setErrors({overlaps: true});
          console.log(this.reservationRange)
        }else{
          this.reservationRange.get('start')?.setErrors(null);
          this.reservationRange.get('end')?.setErrors(null);
          console.log(this.reservationRange)
        }
    }
  }

  /**
   * This method is used when user is trying to book a new reservation
   * depending of the input value a new room is going to be added to
   * the list of room for the new reservation, or the opposite, remove
   * and existing room since user decided to uncheck it.
   * @param event Checkbox value checked = true unchecked = false
   */
  selectionChanged(event: MatCheckboxChange): void{
    if(event.checked){
      if(this.roomAvailability && this.room && this.reservationRange.valid){
        let from = this.datePipe.transform(this.reservationRange.get('start')?.value,'yyyy-MM-dd');
        let to = this.datePipe.transform(this.reservationRange.get('end')?.value,'yyyy-MM-dd');
        if(from && to){
          let reservationRoomEvent: ReservationRoomEvent = {
            action: 'ADD',
            reservationRoom: {
              room: this.room,
            stayFrom: this.reservationRange.get("start")?.value,
            stayTo: this.reservationRange.get("end")?.value,
            status: 1
            }
          }
          this.reservationChange.emit(reservationRoomEvent)
        }
      }
    }else{
      if(this.roomAvailability && this.room && this.reservationRange.valid){
        let reservationRoomEvent: ReservationRoomEvent = {
          action: 'REMOVE',
          reservationRoom: {
            room: this.room,
          stayFrom: this.reservationRange.get("start")?.value,
          stayTo: this.reservationRange.get("end")?.value,
          status: 1
          }
        }
        this.reservationChange.emit(reservationRoomEvent)
      }
    }
  }

}
