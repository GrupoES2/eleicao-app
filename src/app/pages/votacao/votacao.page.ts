import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CandidatoService } from '../../services/candidato';
import { EleicaoService } from '../../services/eleicao';
import { VotoService } from '../../services/voto';
import { AuthService } from '../../services/auth.service';
import { 
  IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardContent, 
  IonButton, IonSpinner, IonList, IonItem, IonLabel ,IonHeader, IonToolbar, IonTitle
} from '@ionic/angular';

@Component({
  selector: 'app-votacao',
  templateUrl: './votacao.page.html',
  styleUrls: ['./votacao.page.scss'],
  standalone: true,
  imports: [CommonModule, IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardContent,
     IonButton, IonSpinner, IonList, IonItem, IonLabel, IonHeader, IonToolbar, IonTitle]
})
export class VotacaoPage implements OnInit {
  eleicoesAbertas: any[] = [];
  candidatos: any[] = [];
  cargosJaVotados: string[] = []; // Guarda os cargos que o eleitor já votou
  carregando = true;
  mensagem = '';

  constructor(
    private candidatoService: CandidatoService,
    private eleicaoService: EleicaoService,
    private votoService: VotoService,
    private authService: AuthService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.carregarDados();
  }

  carregarDados() {
    this.carregando = true;
    const usuario = this.authService.usuarioLogadoValue;

    this.eleicaoService.listar().subscribe(eleicoes => {
      this.eleicoesAbertas = eleicoes.filter(e => e.status === 'ABERTA');
      
      this.candidatoService.listar().subscribe(cands => {
        this.candidatos = cands;
        
        // Busca os votos do eleitor para saber o que ele já votou
        if (usuario) {
          this.votoService.buscarVotosDoEleitor(usuario.id).subscribe(votos => {
            // Preenche a lista com os cargos votados
            this.cargosJaVotados = votos.map(v => v.cargo);
            this.carregando = false;
            this.cd.detectChanges();
          });
        } else {
          this.carregando = false;
          this.cd.detectChanges();
        }
      });
    });
  }

  candidatosDoCargo(cargo: string) {
    return this.candidatos.filter(c => c.cargo === cargo);
  }

  jaVotouNoCargo(cargo: string): boolean {
    return this.cargosJaVotados.includes(cargo);
  }
  votar(eleicao: any, candidato: any) {
    const usuario = this.authService.usuarioLogadoValue;
    if (!usuario) return;

    const novoVoto = {
      eleitorId: usuario.id,
      eleicaoId: eleicao.id,
      candidatoId: candidato.id,
      cargo: candidato.cargo
    };

       this.votoService.registrarVoto(novoVoto).subscribe({
      next: () => {
        // Soma 1 voto no candidato e salva no banco
        candidato.votos = (candidato.votos || 0) + 1;
        this.candidatoService.atualizarVotos(candidato).subscribe();

        // Mensagem sigilosa
        this.mensagem = `Voto registrado com sucesso!`;
        this.cargosJaVotados.push(candidato.cargo);
        this.cd.detectChanges();

        // O TEMPORIZADOR AQUI: Apaga a mensagem depois de 3 segundos (3000 milissegundos)
        setTimeout(() => {
          this.mensagem = '';
          this.cd.detectChanges();
        }, 2000);
      },
      error: () => alert('Erro ao registrar voto.')
    });
}
}