import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class VotoService {
  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) {}

  buscarVotosDoEleitor(eleitorId: string | number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/votos?eleitorId=${eleitorId}`);
  }

  registrarVoto(voto: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/votos`, voto);
  }
}