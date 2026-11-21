import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AlertController } from '@ionic/angular';
import { addIcons } from 'ionicons';
import { logOutOutline, personCircleOutline, keyOutline, informationCircleOutline } from 'ionicons/icons';
import { 
  IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardContent, 
  IonSpinner, IonBadge, IonRefresher, IonRefresherContent, IonButton, IonIcon, IonText,
  IonButtons, IonList, IonItem, IonLabel, IonHeader, IonToolbar, IonTitle,IonTabs,IonTabBar, IonTabButton, IonTab, IonRouterOutlet
} from '@ionic/angular';

@Component({
  selector: 'app-tabs-chefe',
  templateUrl: './tabs-chefe.page.html',
  styleUrls: ['./tabs-chefe.page.scss'],
  standalone: true,
   imports: [IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonSpinner, 
    IonBadge, IonRefresher, IonRefresherContent, IonButton, IonIcon, IonText, IonButtons, IonList, 
    IonItem, IonLabel, IonHeader, IonToolbar, IonTitle,IonTabs, IonTabBar, IonTabButton, IonTab, IonRouterOutlet]
})
export class TabsChefePage {
  constructor(
    private authService: AuthService, 
    private router: Router,
    private alertController: AlertController
  ) {
    addIcons({ logOutOutline, personCircleOutline, keyOutline, informationCircleOutline });
  }

  sair() {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }

  // Função que abre o menu de perfil
   async abrirMenuPerfil(ev: any) {
    const usuario = this.authService.usuarioLogadoValue;
    
    const alerta = await this.alertController.create({
      header: 'Meu Perfil',
      subHeader: `Logado como: ${usuario?.nome}`,
      buttons: [
        {
          text: 'Editar Perfil',
          handler: () => this.editarPerfil() 
        },
        {
          text: 'Cancelar',
          role: 'cancel'
        }
      ]
    });
    await alerta.present();
  }
  // Função de alterar a senha
  async editarPerfil() {
    const usuario = this.authService.usuarioLogadoValue;
    if (!usuario) return;

    const alerta = await this.alertController.create({
      header: 'Editar Meu Perfil',
      inputs: [
        {
          name: 'nome',
          type: 'text',
          value: usuario.nome,
          placeholder: 'Nome'
        },
        {
          name: 'login',
          type: 'text',
          value: usuario.login,
          placeholder: 'Login'
        },
        {
          name: 'senha',
          type: 'password',
          value: usuario.senha,
          placeholder: 'Nova Senha'
        }
      ],
      buttons: [
        { text: 'Cancelar', role: 'cancel' },
        {
          text: 'Salvar',
          handler: (data) => {
            if (data.nome && data.login && data.senha) {
              const usuarioAtualizado = { 
                ...usuario, 
                nome: data.nome, 
                login: data.login, 
                senha: data.senha 
              };
              
              this.authService.atualizarUsuario(String(usuario.id), usuarioAtualizado).subscribe({
                next: () => {
                  alert('Perfil atualizado com sucesso!');
                  localStorage.setItem('usuario', JSON.stringify(usuarioAtualizado));
                  
                  // RECARREGAMENTO DA PÁGINA
                  window.location.reload(); 
                },
                error: () => alert('Erro ao atualizar perfil.')
              });
            } else {
              alert('Preencha todos os campos!');
            }
          }
        }
      ]
    });
    await alerta.present();
  }
}