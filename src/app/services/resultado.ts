import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ResultadoService {
  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) {}

  // Pega todos os candidatos com seus votos
  buscarResultados(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/candidatos`);
  }
}