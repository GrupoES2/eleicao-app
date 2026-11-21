import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.page').then((m) => m.LoginPage),
  },
  {
    path: 'integrantes',
    loadComponent: () => import('./pages/integrantes/integrantes.page').then((m) => m.IntegrantesPage),
  },

  // --- ABAS DO CHEFE ---
  {
    path: 'tabs-chefe',
    loadComponent: () => import('./pages/tabs-chefe/tabs-chefe.page').then((m) => m.TabsChefePage),
    children: [
      {
        path: 'gerenciar',
        loadComponent: () =>
          import('./pages/gerenciar-eleicao/gerenciar-eleicao.page').then((m) => m.GerenciarEleicaoPage),
      },
      {
        path: 'cadastrar',
        loadComponent: () =>
          import('./pages/cadastrar-usuario/cadastrar-usuario.page').then((m) => m.CadastrarUsuarioPage),
      },
      {
        path: 'resultados',
        loadComponent: () => import('./pages/resultados/resultados.page').then((m) => m.ResultadosPage),
      },
      {
        path: '',
        redirectTo: '/tabs-chefe/gerenciar',
        pathMatch: 'full',
      },
    ],
  },

  // --- ABAS DO ELEITOR ---
  {
    path: 'tabs-eleitor',
    loadComponent: () => import('./pages/tabs-eleitor/tabs-eleitor.page').then((m) => m.TabsEleitorPage),
    children: [
      {
        path: 'votar',
        loadComponent: () => import('./pages/votacao/votacao.page').then((m) => m.VotacaoPage),
      },
      {
        path: 'resultados',
        loadComponent: () => import('./pages/resultados/resultados.page').then((m) => m.ResultadosPage),
      },
      {
        path: '',
        redirectTo: '/tabs-eleitor/votar',
        pathMatch: 'full',
      },
    ],
  },
  {
    path: 'tabs-candidato',
    loadComponent: () => import('./pages/tabs-candidato/tabs-candidato.page').then((m) => m.TabsCandidatoPage),
    children: [
      {
        path: 'meus-resultados',
        loadComponent: () => import('./pages/resultados/resultados.page').then((m) => m.ResultadosPage),
      },
      {
        path: '',
        redirectTo: '/tabs-candidato/meus-resultados',
        pathMatch: 'full',
      },
    ],
  },
];
