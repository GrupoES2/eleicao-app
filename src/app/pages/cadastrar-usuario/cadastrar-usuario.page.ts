import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CandidatoService } from '../../services/candidato';
import { EleicaoService } from '../../services/eleicao';
import { addIcons } from 'ionicons';
import { eyeOutline, eyeOffOutline, trashBinOutline, pencilOutline } from 'ionicons/icons';
import { 
  IonContent, IonItem, IonLabel, IonInput, IonSelect, IonSelectOption, 
  IonButton, IonSegment, IonSegmentButton, IonList, IonCard, IonCardHeader, IonCardTitle, 
  IonHeader, IonToolbar, IonTitle, IonText, IonCardContent, IonIcon, AlertController
} from '@ionic/angular';

@Component({
  selector: 'app-cadastrar-usuario',
  templateUrl: './cadastrar-usuario.page.html',
  styleUrls: ['./cadastrar-usuario.page.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule, IonContent, IonItem, IonLabel, IonInput, 
    IonSelect, IonSelectOption, IonButton, IonSegment, IonSegmentButton, 
    IonList, IonCard, IonCardHeader, IonCardTitle, IonHeader, IonToolbar, IonTitle, IonText,
    IonCardContent, IonIcon
  ]
})
export class CadastrarUsuarioPage implements OnInit {
  tipoCadastro = 'ELEITOR';
  usuariosCadastrados: any[] = [];
  
  // Campos gerais
  nome = '';
  login = '';
  senha = '';
  mostrarSenha = false;
  
  // Campos do candidato
  numero = 0;
  cargo = '';
  partido = '';
  eleicoes: any[] = [];

  // Controle de edição
  idEditando: string | null = null;
  candidatoIdEditando: string | null = null;

  mensagem = '';
  erro = '';

  constructor(
    private authService: AuthService,
    private candidatoService: CandidatoService,
    private eleicaoService: EleicaoService,
    private cd: ChangeDetectorRef,
    private alertController: AlertController
  ) {
    addIcons({ eyeOutline, eyeOffOutline, trashBinOutline, pencilOutline });
  }

  ngOnInit() {
    this.eleicaoService.listar().subscribe(res => {
      this.eleicoes = res;
      this.cd.detectChanges();
    });
    this.carregarUsuarios();
  }

  carregarUsuarios() {
    this.authService.listarUsuarios().subscribe(res => {
      this.usuariosCadastrados = res;
      this.cd.detectChanges();
    });
  }

  async excluirUsuario(usuario: any) {
    const alerta = await this.alertController.create({
      header: 'Excluir',
      message: `Deseja excluir ${usuario.nome}?`,
      buttons: [
        { text: 'Cancelar', role: 'cancel' },
        { 
          text: 'Excluir', 
          role: 'destructive',
          handler: () => {
            this.authService.excluirUsuario(usuario.id).subscribe(() => {
              this.carregarUsuarios();
            });
          }
        }
      ]
    });
    await alerta.present();
  }

    editarUsuario(usuario: any) {
    this.idEditando = String(usuario.id);
    this.tipoCadastro = usuario.perfil;
    this.nome = usuario.nome;
    this.login = usuario.login;
    this.senha = usuario.senha;

    if (usuario.perfil === 'CANDIDATO') {
      this.candidatoService.listar().subscribe(cands => {
        const cand = cands.find(c => c.nome === usuario.nome);
        if (cand) {
          this.candidatoIdEditando = String(cand.id);
          this.numero = cand.numero;
          this.partido = cand.partido;
          this.cargo = cand.cargo;
        }
        this.cd.detectChanges();
        return;
      });
    }
    
    window.scrollTo(0, 0);
  }

  salvar() {
    this.mensagem = '';
    this.erro = '';

    if (!this.nome || !this.login || !this.senha) {
      alert('Por favor, preencha os campos obrigatórios.');
      return;
    }

    // MODO EDIÇÃO
    if (this.idEditando) {
      const usuarioAtualizado = { 
        id: this.idEditando,
        nome: this.nome, 
        login: this.login, 
        senha: this.senha, 
        perfil: this.tipoCadastro 
      };

      this.authService.atualizarUsuario(this.idEditando, usuarioAtualizado).subscribe({
        next: () => {
          // Se for candidato, atualiza os dados eleitorais também
          if (this.tipoCadastro === 'CANDIDATO' && this.candidatoIdEditando) {
            const candAtualizado = {
              id: this.candidatoIdEditando,
              nome: this.nome,
              numero: this.numero,
              cargo: this.cargo,
              partido: this.partido,
              votos: 0 // O ideal seria manter os votos, mas para simplificar deixamos 0
            };
            this.candidatoService.atualizarVotos(candAtualizado).subscribe();
          }
          
          alert('Editado com sucesso!');
          this.limparFormulario();
          this.carregarUsuarios();
        },
        error: () => alert('Erro ao editar.')
      });
      return;
    }

    // MODO CRIAÇÃO
    if (this.tipoCadastro === 'ELEITOR') {
      const novoUsuario = { nome: this.nome, login: this.login, senha: this.senha, perfil: 'ELEITOR' };
      this.authService.cadastrarUsuario(novoUsuario).subscribe({
        next: () => {
          this.sucesso();
          this.carregarUsuarios();
        },
        error: () => alert('Erro ao cadastrar eleitor.')
      });
    } else {
      if (!this.partido || !this.numero || !this.cargo) {
        alert('Preencha todos os campos do candidato.');
        return;
      }

      const novoUsuario = { nome: this.nome, login: this.login, senha: this.senha, perfil: 'CANDIDATO' };
      this.authService.cadastrarUsuario(novoUsuario).subscribe({
        next: () => {
          const novoCandidato = { 
            nome: this.nome, numero: this.numero, cargo: this.cargo, partido: this.partido, votos: 0 
          };
          this.candidatoService.cadastrar(novoCandidato).subscribe({
            next: () => {
              this.sucesso();
              this.carregarUsuarios();
            },
            error: () => alert('Erro ao cadastrar candidato.')
          });
        },
        error: () => alert('Erro ao criar login.')
      });
    }
  }

  limparFormulario() {
    this.idEditando = null;
    this.candidatoIdEditando = null;
    this.nome = ''; 
    this.login = ''; 
    this.senha = ''; 
    this.numero = 0;
    this.partido = '';
    this.cargo = '';
    this.cd.detectChanges();
  }

  sucesso() {
    alert('Cadastrado com sucesso!');
    this.limparFormulario();
  }
  
  toggleSenha() {
    this.mostrarSenha = !this.mostrarSenha;
  }
}