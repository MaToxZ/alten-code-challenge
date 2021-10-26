import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Booking } from '../model/booking';
import { PaginatedResult } from '../model/paginatedResult';
import { RoomAvailability } from '../model/roomAvailability';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  constructor(
    private http: HttpClient
  ) { }

  findReservation(id: string): Observable<Booking>{
    const url = `${environment.apiUrl}/reservation/${id}`;
    return this.http.get<Booking>(url);
  }

  book(book: Booking | undefined): Observable<Booking>{
    const url = `${environment.apiUrl}/reservation/book`;
    return this.http.post<Booking>(url,book);
  }

  modify(book: Booking | undefined): Observable<Booking>{
    const url = `${environment.apiUrl}/reservation/modify`;
    return this.http.patch<Booking>(url,book,{
      headers:{
        'Content-Type': 'application/json'
      }
    });
  }

  cancel(id: string): Observable<string>{
    const url = `${environment.apiUrl}/reservation/${id}/cancel`;
    return this.http.delete<string>(
      url,
      {
        responseType: 'text' as 'json',
      }
    );
  }
}
