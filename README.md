# Sistema de Eleição Eletrônica

Este repositório contém o projeto do Sistema de Eleição Eletrônica. 
O sistema permite o gerenciamento de eleições, cadastro de usuários e a realização de votações com apuração de resultados em tempo real.

## Tecnologias Utilizadas
**Front-End:**
- Ionic Framework 
- Angular
- TypeScript
- CSS
- HTML

**Back-End:**
- Java
- Spring Boot
- SQLite


## Pré-requisitos
Para rodar o projeto completo, você precisará ter instalado:
- [Node.js](https://nodejs.org/) 
- [Ionic CLI](https://ionicframework.com/docs/cli) (`npm install -g @ionic/cli`)
- [Java JDK](https://www.oracle.com/java/technologies/downloads/) 
- [Maven](https://maven.apache.org/) ou Gradle (Spring Boot)

## Como Executar o Projeto

### Passo 1: Rodar o Back-End (Spring Boot)
1. **
   
### Passo 2: Rodar o Front-End (Ionic)
1. Navegue até a pasta do back-end no terminal:
   ```bash
   cd NOME_DA_PASTA_DO_BACKEND
### Passo 3: 
1. Na raiz do projeto Front-End, instale as dependências:
   ```bash
   npm install
### Passo 4:
1. Na raiz do projeto Front-End, instale as dependências:
   ```bash
   ionic serve

## Arquitetura do Sistema
O sistema foi dividido em duas camadas principais:
- **Back-End (Spring Boot):** Responsável por conter as regras de negócio, segurança e persistência no banco de dados. Ele expõe uma API REST que é consumida pelo Front-End.
- **Front-End (Ionic/Angular):** Responsável pela interface do usuário (Mobile). Ele se comunica com o Back-End através de requisições HTTP (Services do Angular), recebendo e enviando dados em formato JSON.

## Modularização
**No Front-End:** O projeto está dividido em:
- `pages`: Contém as telas do aplicativo (login, votação, tabs-chefe, etc).
- `services`: Responsável pela comunicação com a API (AuthService, eleicao, etc).
- `models`: Contém as interfaces TypeScript que definem o formato dos dados (usuario, eleicao, candidato, voto).
- `app.routes.ts`: Faz a navegação entre as telas e define quais telas cada perfil acessa.

**No Back-End (Spring Boot):**
- **.

## 🗄️ Modelagem do Banco de Dados
O sistema é composto pelas seguintes entidades principais:
- **
