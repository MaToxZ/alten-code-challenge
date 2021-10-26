import { DatePipe } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';


import * as _moment from 'moment';
import { Moment } from 'moment';
import { RoomService } from 'src/app/services/room.service';

const moment = _moment;

@Component({
  selector: 'app-booking-modal',
  templateUrl: './booking-modal.component.html',
  styleUrls: ['./booking-modal.component.scss']
})
export class BookingModalComponent implements OnInit {

  visitorGroup: FormGroup = new FormGroup({
    name: new FormControl('', [
      Validators.required
    ]),
    email: new FormControl('',[
      Validators.required,
      Validators.email
    ]),
    phone: new FormControl('',[
      Validators.required,
      Validators.pattern('[- +()0-9]+')
    ])
  });

  availabilityRange = new FormGroup({
    start: new FormControl('',Validators.required),
    end: new FormControl('',Validators.required),
  });

  operation: string = "";
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<BookingModalComponent>,
  ) { }

  ngOnInit(): void {
    this.operation = this.data.operation;
  }

  cancel(): void{
    this.dialogRef.close();
  }

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

  /**
   * This method is trigger when a value for END (date range) is provided
   * @param reservationRange FormGroup containig both start and end date range
   */
  dateRangeChange(reservationRange: FormGroup){
    this.closeModal()
  }

  /**
   * This method is going to return the related date from this component depending on the operation provided when this was instatiated
   */
  closeModal(): void{
    switch (this.operation ) {
      case 'visitorInfo':{
        this.dialogRef.close({
          name: this.visitorGroup.get('name')?.value,
          email: this.visitorGroup.get('email')?.value,
          phone: this.visitorGroup.get('phone')?.value,
          operation: 'visitorInfo'
        });
      }break;
      case 'availability':{
        let start = this.availabilityRange.get('start')?.value;
        let end = this.availabilityRange.get('end')?.value;
        if(start && end){
          this.dialogRef.close({
            from: start,
            to: end,
            operation: 'availability'
          })
        }
      }break;
    }
  
  }

}
