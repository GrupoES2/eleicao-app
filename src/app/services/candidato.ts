import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Candidato } from '../models/candidato.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class CandidatoService {
  private apiUrl = environment.apiUrl;
  constructor(private http: HttpClient) {}

  listar(): Observable<Candidato[]> {
    return this.http.get<Candidato[]>(`${this.apiUrl}/candidatos`);
  }

  cadastrar(candidato: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/candidatos`, candidato);
  }
   atualizarVotos(candidato: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/candidatos/${candidato.id}`, candidato);
  }
}