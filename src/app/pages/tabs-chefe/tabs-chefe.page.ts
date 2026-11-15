import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { 
  IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonIcon,
  IonTabs, IonTabBar, IonTabButton, IonLabel 
} from '@ionic/angular';

@Component({
  selector: 'app-tabs-chefe',
  templateUrl: './tabs-chefe.page.html',
  styleUrls: ['./tabs-chefe.page.scss'],
  standalone: true,
  imports: [
    IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonIcon,
    IonTabs, IonTabBar, IonTabButton, IonLabel
  ]
})
export class TabsChefePage {
  constructor(private authService: AuthService, private router: Router) {}

  sair() {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }
}