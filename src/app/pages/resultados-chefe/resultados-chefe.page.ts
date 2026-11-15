import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonHeader, IonTitle, IonToolbar } from '@ionic/angular';

@Component({
  selector: 'app-resultados-chefe',
  templateUrl: './resultados-chefe.page.html',
  styleUrls: ['./resultados-chefe.page.scss'],
  imports: [IonContent, IonHeader, IonTitle, IonToolbar, CommonModule, FormsModule],
})
export class ResultadosChefePage implements OnInit {
  constructor() {}

  ngOnInit() {}
}
