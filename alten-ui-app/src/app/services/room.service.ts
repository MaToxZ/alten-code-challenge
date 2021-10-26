import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { PaginatedResult } from '../model/paginatedResult';
import { RoomAvailability } from '../model/roomAvailability';

@Injectable({
  providedIn: 'root'
})
export class RoomService {

  constructor(
    private http: HttpClient
  ) { }

  findRoomsAvailability(from: any, to: any): Observable<PaginatedResult<RoomAvailability>>{
    const url = `${environment.apiUrl}/room/availability?stayFrom=${from}&stayTo=${to}`;
    return this.http.get<PaginatedResult<RoomAvailability>>(url);
  }
}
