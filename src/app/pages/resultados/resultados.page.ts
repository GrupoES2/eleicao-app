import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResultadoService } from '../../services/resultado';
import { EleicaoService } from '../../services/eleicao';
import { AuthService } from '../../services/auth.service';
import { 
  IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardContent, 
  IonSpinner, IonBadge, IonRefresher, IonRefresherContent, IonButton, IonIcon, IonText,
  IonButtons, IonList, IonItem, IonLabel, IonHeader, IonToolbar, IonTitle
} from '@ionic/angular';

@Component({
  selector: 'app-resultados',
  templateUrl: './resultados.page.html',
  styleUrls: ['./resultados.page.scss'],
  standalone: true,
  imports: [CommonModule, IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonSpinner, 
    IonBadge, IonRefresher, IonRefresherContent, IonButton, IonIcon, IonText, IonButtons, IonList, 
    IonItem, IonLabel, IonHeader, IonToolbar, IonTitle]
})
export class ResultadosPage implements OnInit {
  cargosAgrupados: any[] = [];
  carregando = true;
  perfilUsuario = '';
  nomeCandidatoLogado = '';

  constructor(
    private resultadoService: ResultadoService,
    private eleicaoService: EleicaoService,
    private authService: AuthService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const usuario = this.authService.usuarioLogadoValue;
    if (usuario) {
      this.perfilUsuario = usuario.perfil;
      this.nomeCandidatoLogado = usuario.nome;
    }
  }

  ionViewWillEnter() {
    this.carregarResultados();
  }

  carregarResultados(event?: any) {
    if (!event) this.carregando = true;

    this.eleicaoService.listar().subscribe(eleicoes => {
      this.resultadoService.buscarResultados().subscribe({
        next: (listaCandidatos) => {
          
          // 1. Filtra os candidatos se o perfil for CANDIDATO
          let candidatosVisiveis = listaCandidatos;
          let eleicoesVisiveis = eleicoes;

          if (this.perfilUsuario === 'CANDIDATO') {
            const eu = listaCandidatos.find(c => c.nome === this.nomeCandidatoLogado);
            if (eu) {
              candidatosVisiveis = listaCandidatos.filter(c => c.cargo === eu.cargo);
              eleicoesVisiveis = eleicoes.filter(e => e.cargo === eu.cargo);
            } else {
              candidatosVisiveis = [];
              eleicoesVisiveis = [];
            }
          }

          // 2. Agrupa usando a lista de ELEIÇÕES como base (para não esconder cargos vazios)
          const agrupados = [];

          for (const eleicao of eleicoesVisiveis) {
            const candidatosDoCargo = candidatosVisiveis.filter(c => c.cargo === eleicao.cargo);
            
            // Descobre o vencedor (mais votos)
            let vencedor = null;
            if (candidatosDoCargo.length > 0) {
              const maxVotos = Math.max(...candidatosDoCargo.map(c => c.votos || 0));
              if (maxVotos > 0) {
                vencedor = candidatosDoCargo.find(c => (c.votos || 0) === maxVotos);
              }
            }

            agrupados.push({
              cargo: eleicao.cargo,
              status: eleicao.status,
              vencedor: vencedor,
              candidatos: candidatosDoCargo,
              expandido: false
            });
          }

          this.cargosAgrupados = agrupados;
          this.carregando = false;
          this.cd.detectChanges();
          if (event) event.target.complete();
        },
        error: () => {
          this.carregando = false;
          this.cd.detectChanges();
          if (event) event.target.complete();
        }
      });
    });
  }

  toggleCandidatos(item: any) {
    item.expandido = !item.expandido;
  }
}