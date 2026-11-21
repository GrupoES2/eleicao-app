import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { 
  IonHeader, IonToolbar, IonTitle, IonContent, IonItem, 
  IonLabel, IonInput, IonButton , IonIcon
} from '@ionic/angular';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { addIcons } from 'ionicons';
import {  eyeOutline, eyeOffOutline, peopleOutline } from 'ionicons/icons';




@Component({
  selector: 'app-login',
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, 
    IonHeader, IonToolbar, IonTitle, IonContent, IonItem, 
    IonLabel, IonInput, IonButton,IonIcon
  ]
})
export class LoginPage {
  login = '';
  senha = '';
  erro = '';
  carregando = false;
 mostrarSenha = false;

  constructor(
    private authService: AuthService, 
    private router: Router
  ) {  
    addIcons({  eyeOutline, eyeOffOutline, peopleOutline });
    }

  entrar(){
  this.erro = '';
  if(!this.login || !this.senha){
    this.erro = 'Por favor, preencha todos os campos.';
    return;
  }
  this.carregando = true;

  this.authService.login(this.login, this.senha).subscribe({
   next: (usuario: any) => {
        this.carregando = false;
        if (usuario.perfil === 'CHEFE') {
          this.router.navigateByUrl('/tabs-chefe');
        } else if (usuario.perfil === 'ELEITOR') {
          this.router.navigateByUrl('/tabs-eleitor');
        } else if (usuario.perfil === 'CANDIDATO') { 
          this.router.navigateByUrl('/tabs-candidato');
        } else {
          alert('Perfil não reconhecido');
        }
    },
    error: (err: any) => {
      this.carregando = false;
      this.erro = err.message || 'Falha na comunicação com a API';}
  });
}
clean(){
  this.carregando = false;
  this.erro = '';
  this.login = '';
  this.senha = '';
}
integrantes(){
  this.router.navigateByUrl('/integrantes');  
} 
toggleSenha() {
    this.mostrarSenha = !this.mostrarSenha;
  }
} 