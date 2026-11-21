import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EleicaoService } from '../../services/eleicao';
import { Eleicao } from '../../models/eleicao.model';
import { 
  IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardContent, 
  IonButton, IonSpinner, IonIcon, AlertController, IonList, IonItem, IonLabel, IonInput
  ,IonHeader, IonToolbar, IonTitle,IonBadge, IonFab, IonFabButton
} from '@ionic/angular';
import { addIcons } from 'ionicons';
import { addOutline, trashBinOutline } from 'ionicons/icons';

@Component({
  selector: 'app-gerenciar-eleicao',
  templateUrl: './gerenciar-eleicao.page.html',
  styleUrls: ['./gerenciar-eleicao.page.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, IonContent, IonCard, IonCardHeader, IonCardTitle,
     IonCardContent, IonButton, IonSpinner, IonIcon, IonHeader,
      IonToolbar, IonTitle,IonButton,IonBadge, IonFab, IonFabButton
    ]
})
export class GerenciarEleicaoPage implements OnInit {
  eleicoes: Eleicao[] = [];
  carregando = true;

  constructor(
    private eleicaoService: EleicaoService, 
    private cd: ChangeDetectorRef,
    private alertController: AlertController
  ) {addIcons({ addOutline, trashBinOutline });}

  ngOnInit() {
    this.carregarStatus();
  }

  carregarStatus() {
    this.carregando = true;
    this.eleicaoService.listar().subscribe({
      next: (lista) => {
        this.eleicoes = lista;
        this.carregando = false;
        this.cd.detectChanges();
      },
      error: () => {
        this.carregando = false;
        this.cd.detectChanges();
      }
    });
  }

  async criarEleicao() {
    const alerta = await this.alertController.create({
      header: 'Cadastrar Nova Eleição',
      inputs: [
        { name: 'cargo', type: 'text', placeholder: 'Cargo (Ex: Presidente)' },
        { name: 'data', type: 'date', placeholder: 'Data' },
        { name: 'turno', type: 'text', placeholder: 'Turno (Ex: Único)' },
        { name: 'abertura', type: 'time', placeholder: 'Horário Abertura' },
        { name: 'encerramento', type: 'time', placeholder: 'Horário Encerramento' }
      ],
      buttons: [
        { text: 'Cancelar', role: 'cancel' },
        {
          text: 'Criar',
          handler: (data) => {
            if (data.cargo && data.data && data.turno && data.abertura && data.encerramento) {
              const novaEleicao = {
                cargo: data.cargo,
                data: data.data,
                turno: data.turno,
                horarioAbertura: data.abertura,
                horarioEncerramento: data.encerramento,
                status: 'AGENDADA' // Sempre nasce agendada
              };
              
              this.eleicaoService.criar(novaEleicao).subscribe({
                next: () => this.carregarStatus(),
                error: () => alert('Erro ao criar eleição.')
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

  async excluirEleicao(eleicao: Eleicao) {
    const alerta = await this.alertController.create({
      header: 'Confirmar Exclusão',
      message: `Deseja excluir a eleição para ${eleicao.cargo}?`,
      buttons: [
        { text: 'Cancelar', role: 'cancel' },
        {
          text: 'Excluir',
          role: 'destructive',
          handler: () => {
            this.eleicaoService.excluir(eleicao.id).subscribe({
              next: () => this.carregarStatus(),
              error: () => alert('Erro ao excluir.')
            });
          }
        }
      ]
    });
    await alerta.present();
  }

  // Muda o status de AGENDADA -> ABERTA ou ABERTA -> ENCERRADA
  mudarStatus(eleicao: Eleicao, novoStatus: string) {
    this.eleicaoService.atualizarStatus(eleicao.id, novoStatus).subscribe({
      next: () => {
        eleicao.status = novoStatus as any;
        this.cd.detectChanges();
      },
      error: () => alert('Erro ao mudar o status.')
    });
  }
}