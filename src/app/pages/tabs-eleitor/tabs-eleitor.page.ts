import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { addIcons } from 'ionicons';
import { logOutOutline, checkboxOutline, barChartOutline } from 'ionicons/icons';
import { 
  IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonIcon,
  IonTabs, IonTabBar, IonTabButton, IonLabel 
} from '@ionic/angular';

@Component({
  selector: 'app-tabs-eleitor',
  templateUrl: './tabs-eleitor.page.html',
  styleUrls: ['./tabs-eleitor.page.scss'],
  standalone: true,
  imports: [IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonIcon, IonTabs, IonTabBar, IonTabButton, IonLabel]
})
export class TabsEleitorPage {
  constructor(private authService: AuthService, private router: Router) {
    // Registra os ícones que aparecem na barra de baixo
    addIcons({ logOutOutline, checkboxOutline, barChartOutline });
  }

  sair(){
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}