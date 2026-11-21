import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { addIcons } from 'ionicons';
import { logOutOutline, barChartOutline } from 'ionicons/icons';
import { 
  IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonIcon,
  IonTabs, IonTabBar, IonTabButton, IonLabel 
} from '@ionic/angular';

@Component({
  selector: 'app-tabs-candidato',
  templateUrl: './tabs-candidato.page.html',
  styleUrls: ['./tabs-candidato.page.scss'],
  standalone: true,
  imports: [IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonIcon, IonTabs, IonTabBar, IonTabButton, IonLabel]
})
export class TabsCandidatoPage {
  constructor(private authService: AuthService, private router: Router) {
    addIcons({ logOutOutline, barChartOutline });
  }

  sair() {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}