import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Eleicao } from '../models/eleicao.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class EleicaoService {
  private apiUrl = environment.apiUrl;
  
  constructor(private http: HttpClient) {}

  listar(): Observable<Eleicao[]> {
    return this.http.get<Eleicao[]>(`${this.apiUrl}/eleicoes`);
  }

  criar(eleicao: any): Observable<Eleicao> {
    return this.http.post<Eleicao>(`${this.apiUrl}/eleicoes`, eleicao);
  }

  atualizarStatus(id: string | number, status: string): Observable<Eleicao> {
    return this.http.patch<Eleicao>(`${this.apiUrl}/eleicoes/${id}`, { status: status });
  }

  excluir(id: string | number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/eleicoes/${id}`);
  }
}