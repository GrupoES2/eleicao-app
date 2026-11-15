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
    path: 'home-eleitor',
    loadComponent: () => import('./pages/home-eleitor/home-eleitor.page').then((m) => m.HomeEleitorPage),
  },
  {
    path: 'home-candidato',
    loadComponent: () => import('./pages/home-candidato/home-candidato.page').then((m) => m.HomeCandidatoPage),
  },
  
  // --- AQUI ESTÁ A MÁGICA DAS ABAS (PAI E FILHOS) ---
  {
    path: 'tabs-chefe',
    loadComponent: () => import('./pages/tabs-chefe/tabs-chefe.page').then((m) => m.TabsChefePage),
    children: [
      {
        path: 'gerenciar',
        loadComponent: () => import('./pages/gerenciar-eleicao/gerenciar-eleicao.page').then((m) => m.GerenciarEleicaoPage),
      },
      {
        path: 'cadastrar',
        loadComponent: () => import('./pages/cadastrar-usuario/cadastrar-usuario.page').then((m) => m.CadastrarUsuarioPage),
      },
      {
        path: 'resultados',
        loadComponent: () => import('./pages/resultados-chefe/resultados-chefe.page').then((m) => m.ResultadosChefePage),
      },
      {
        path: '',
        redirectTo: '/tabs-chefe/gerenciar',
        pathMatch: 'full',
      },
    ],
  },
];