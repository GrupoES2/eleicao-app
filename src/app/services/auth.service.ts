import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, map, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Usuario } from '../models/usuario.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private usuarioAtual = new BehaviorSubject<Usuario | null>(null);

  constructor(private http: HttpClient) {
    const salvo = localStorage.getItem('usuario');
    if (salvo) {
      this.usuarioAtual.next(JSON.parse(salvo));
    }
  }

  login(login: string, senha: string): Observable<Usuario> {
       console.log('--- TENTANDO LOGIN ---');
    console.log('URL da API:', this.apiUrl);
    
    return this.http.get<Usuario[]>(`${this.apiUrl}/usuarios`).pipe(
      map(lista => {
        console.log('Dados recebidos do banco:', lista); // Vai mostrar a lista inteira
        
        if (!lista || !Array.isArray(lista)) {
          throw new Error('Erro ao ler dados da API');
        }

        const user = lista.find(u => u.login === login && u.senha === senha);
        
        if (!user) {
          console.log('Usuário não encontrado com essas credenciais.');
          throw new Error('Login ou senha inválidos');
        }
        
        console.log('Usuário encontrado:', user);
        localStorage.setItem('usuario', JSON.stringify(user));
        this.usuarioAtual.next(user);
        return user;
      })
    );
  }

  logout() {
    localStorage.removeItem('usuario');
    this.usuarioAtual.next(null);
  }

  get usuarioLogado() {
    return this.usuarioAtual.asObservable();
  }
   //para cadastrar usuario no chefe-tabs
    cadastrarUsuario(usuario: any) {
    return this.http.post(`${this.apiUrl}/usuarios`, usuario);
  }
   get usuarioLogadoValue() {
    return this.usuarioAtual.value;
  }
    listarUsuarios(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/usuarios`);
  }

  excluirUsuario(id: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/usuarios/${id}`);
  }
    atualizarUsuario(id: string, usuario: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/usuarios/${id}`, usuario);
  }
}