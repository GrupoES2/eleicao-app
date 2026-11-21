# Decisões de arquitetura

Registro do que foi decidido ao separar a votação do monólito, e o motivo de cada escolha.

## A urna é um processo próprio

O cadastro, a eleição e a apuração continuam no monólito (`api/eleicoes-app`, porta 8080). A votação foi para outro processo (`api/votacao`, porta 8081), com banco H2 próprio (`jdbc:h2:mem:votacao`).

Motivo: se o processo único cair, a votação cai junto. O dia da eleição precisa continuar mesmo quando o cadastro ou a apuração falham. Separar só a urna resolve esse risco. Cinco serviços que se chamam a cada voto recriariam o mesmo problema: a queda de um serviço no caminho do voto pararia a urna.

A urna não chama nenhum outro serviço na hora de votar. Ela confere o token localmente e grava com a cópia que já tem.

## O token vale sem o serviço de login

`POST /auth/login`, no monólito, confere a senha e devolve um token HMAC (HS256) com o id do usuário (`sub`) e o cargo (`cargo`), válido por 2 horas. A urna valida a assinatura com o mesmo segredo. Os endpoints do chefe continuam com HTTP Basic.

Motivo: autenticação só acontece no login. Quem já tem token segue votando se o monólito parar. Chamar um serviço de identidade a cada voto faria a queda do login derrubar a eleição.

## A urna guarda uma cópia, não o cadastro

A urna tem réplicas com o mesmo id do monólito, sem gerar id novo:

- eleição local, com status;
- eleitor apto;
- candidato, com nome, número, partido, cargo e eleição.

Um evento só atualiza a cópia quando `ocorridoEm` é posterior a `atualizadoEm`. Evento atrasado é ignorado.

Motivo: a urna precisa decidir sozinha se a eleição está aberta, se o eleitor pode votar e qual candidato existe. Consultar o monólito no voto acoplaria os dois processos de novo. O id é o do cadastro para os dois lados falarem do mesmo registro. Ignorar evento velho evita que uma atualização atrasada apague uma abertura mais nova.

## O voto confirmado não aponta para o eleitor

O fluxo tem dois passos. Registrar grava uma intenção (eleitor, eleição, candidato, cargo). Confirmar, se a intenção bater com o candidato informado, grava:

- o voto, só com eleição e candidato;
- a participação, com eleitor, eleição e cargo, sem candidato;

e apaga a intenção. A participação é única por eleitor, eleição e cargo.

Motivo: o sigilo do minimundo exige impedir voto duplo e, ao mesmo tempo, impedir descobrir depois em quem o eleitor votou. Juntar as duas informações na mesma linha tornaria o voto rastreável. A intenção existe só até a confirmação, porque o voto ainda pode ser trocado antes de confirmar.

## O cadastro publica fatos; a exclusão pergunta

Salvar ou alterar candidato grava um evento na mesma transação do cadastro (`CANDIDATO_ATUALIZADO`). Um publicador, a cada 2 segundos, envia até 20 eventos pendentes para `PUT /interno/candidatos/{id}`, com timeout de 500 ms. Resposta 2xx ou 409 marca o evento como publicado. Qualquer outra falha interrompe o lote e o evento continua na fila.

Excluir candidato chama `GET /interno/candidatos/{id}/possui-votos`. Se a urna não responder, a exclusão é recusada e o candidato permanece. Se já houver voto confirmado, a exclusão também é recusada. Só então o monólito apaga e enfileira `CANDIDATO_REMOVIDO`.

A API interna usa o cabeçalho `X-Chave-Interna`, comparado em tempo constante. O token do eleitor não abre essa API.

Motivo: criar e alterar não podem depender da urna estar no ar; o chefe precisa cadastrar mesmo com a urna desligada, e o fato chega quando ela voltar. Excluir é diferente: apagar um candidato que já recebeu voto perderia o resultado. Por isso a exclusão espera uma resposta. O 409 entra como “publicado” para uma remoção recusada não travar a fila. A chave interna separa tráfego de máquina e tráfego de eleitor.

## O que cada processo responde

A cédula (`GET /eleicoes/{id}/candidatos`) e os votos (`POST /eleicoes/{id}/votos` e `POST /eleicoes/{id}/votos/confirmacao`) ficam na urna. O eleitor precisa do papel `ELEITOR`. A listagem só sai com a eleição aberta na réplica.

O monólito segue com o cadastro de candidatos. Abertura e encerramento de eleição, cadastro de eleitor, apuração e resultado ainda respondem 501.

Motivo: a cédula é dado da urna, porque é ela que está isolada no dia da votação. A apuração pode atrasar ou cair sem impedir o voto, então ela permanece fora do caminho crítico. Eleitor e abertura ainda não publicam eventos; até lá, a réplica só fica aberta e o eleitor só fica apto pela API interna. O evento de candidato leva o status da eleição naquele instante, em geral `AGENDADA`, e a urna recusa voto enquanto a cópia não estiver `ABERTA`.

## Os dois processos não compartilham código

`StatusEleicao` e `TipoCandidato` existem nos dois projetos. A urna não depende do jar do monólito.

Motivo: um jar compartilhado faria a urna recompilar e republicar junto com o cadastro. O contrato entre eles é o JSON da API interna e o token, não as classes Java.

## Segredo e chave ficam na configuração local

O segredo do token e a chave interna estão em `application.properties` dos dois processos, com o mesmo valor. Os testes do monólito usam o perfil `test`, desligam o publicador e apontam a urna para um endereço fechado, para a suíte não depender do outro processo.

Motivo: os dois lados precisam da mesma chave para o token ser verificável offline e para a réplica aceitar o evento. Valor fixo no arquivo serve para desenvolvimento. Em ambiente real, os dois entram por configuração externa, fora do repositório.

## Swagger fica para quando o contrato estiver estável

A disciplina pede documentação da API com Swagger UI ou equivalente. Ainda não há springdoc nem especificação OpenAPI no repositório.

Motivo: vários fluxos ainda respondem 501 (eleitor, abertura, encerramento, apuração e resultado). Gerar a UI agora publicaria um contrato que o servidor não cumpre. Quando esses fluxos existirem, cada processo ganha a própria UI a partir dos controllers. A página pública descreve login, cadastro e votação. A API interna, protegida pela chave de máquina, fica de fora dessa página.

Como a ferramenta funciona, quando for ligada:

- OpenAPI é o contrato: caminhos, métodos, corpos, códigos e autenticação, em JSON.
- O springdoc lê os controllers, as anotações de validação e a segurança, e publica esse JSON em `/v3/api-docs`.
- O Swagger UI é a página que consome esse JSON e monta o formulário para chamar a API. No monólito ela ficará em `http://localhost:8080/swagger-ui.html`. Na urna, em `http://localhost:8081/swagger-ui.html`.
- O código dos controllers é a fonte. `@Operation` e `@Schema` só melhoram o texto exibido. Não há um segundo documento escrito à mão para ficar desatualizado.
