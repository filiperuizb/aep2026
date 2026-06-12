# ObservaAção — Etapa 2 (aplicação web)

Sistema web para registro e acompanhamento de solicitações à prefeitura (iluminação, buracos, zeladoria, saúde, etc.). Foi pensado no contexto da govtech fictícia **ObservaAção**, com foco em **transparência**, **rastreio por protocolo** e **tratamento organizado** das demandas pelo gestor.

No **primeiro bimestre**, o projeto era uma beta em linha de comando (Java puro). No **segundo bimestre**, a solução evoluiu para uma aplicação **full stack**: API REST em **Spring Boot** e interface em **React**.

A motivação do problema liga-se à **ODS 16** (paz, justiça e instituições eficazes): reduzir a opacidade no atendimento, dar previsibilidade de prazos (SLA) e registrar movimentações com responsável — em especial para quem depende do serviço público e não tem “facilitador” informal.

---

## Arquitetura

| Camada | Tecnologias | Pasta |
|--------|-------------|-------|
| Backend | Java 17, Spring Boot 3, Spring Security, JPA, H2 | `backend/` |
| Frontend | React 19, Vite, React Router, Tailwind CSS | `frontend/` |
| Wireframes | Excalidraw e PNGs das telas | `wireframe/AEP/` |

O frontend consome a API em `http://localhost:8080/api`. A autenticação usa sessão HTTP (cookies).

---

## Requisitos

| Item | Versão / observação |
|------|---------------------|
| JDK | 17 ou superior |
| Maven | 3.6+ |
| Node.js | 18 ou superior |
| npm | incluso com o Node |
| Banco | H2 em arquivo local (`backend/data/observacao_db`), criado na primeira execução do backend |

---

## Como executar

É necessário subir **backend** e **frontend** em terminais separados.

### 1. Backend (API)

```bash
cd backend
mvn spring-boot:run
```

A API fica disponível em `http://localhost:8080`. O console do H2 pode ser acessado em `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/observacao_db`).

### 2. Frontend (interface)

```bash
cd frontend
npm install
npm run dev
```

A interface abre em `http://localhost:5173`.

---

## Contas de desenvolvimento

| Perfil | E-mail | Senha |
|--------|--------|-------|
| Gestor | `admin@admin.com` | `123` |
| Cidadão | `cidadao@test.com` | `123` |

Na tela de login também é possível **entrar como anônimo** (sem cadastro) ou usar os atalhos de teste para gestor/cidadão.

---

## O que o sistema faz

- **Cidadão / anônimo:** abrir solicitação (categoria, descrição, localização, bairro, prioridade, anexo opcional como referência de arquivo; opção de anonimato quando logado como cidadão).
- **Cidadão logado:** consultar **minhas solicitações** além de abrir novas demandas.
- **Qualquer pessoa com o protocolo:** acompanhar situação, prazo de SLA, indicação de atraso, justificativa quando existir e histórico de mudanças com **nome do responsável**.
- **Gestor:** ver fila ordenada por prioridade, filtrar por critérios isolados ou combinados, avançar o status **seguindo a sequência** da fila (aberto → triagem → em execução → resolvido → encerrado), com **comentário obrigatório** e **justificativa de atraso** quando o prazo já tiver passado.

---

## Rotas da interface

| Rota | Acesso | Descrição |
|------|--------|-----------|
| `/` | Público | Login e entrada anônima |
| `/acompanhar` | Público | Consulta por protocolo |
| `/cidadao` | Cidadão ou anônimo | Área do cidadão |
| `/cidadao/nova` | Cidadão ou anônimo | Nova solicitação |
| `/cidadao/minhas` | Cidadão logado | Lista de solicitações do usuário |
| `/gestor` | Gestor | Fila, filtros e atualização de status |

---

## API REST (principais endpoints)

| Método | Endpoint | Acesso |
|--------|----------|--------|
| `POST` | `/api/auth/login` | Público |
| `POST` | `/api/auth/logout` | Público |
| `GET` | `/api/enums/categorias`, `/prioridades`, `/status` | Público |
| `POST` | `/api/solicitacoes` | Público |
| `GET` | `/api/solicitacoes/protocolo/{protocolo}` | Público |
| `GET` | `/api/solicitacoes/usuario/{id}` | Autenticado |
| `GET` | `/api/solicitacoes/fila` | Gestor |
| `POST` | `/api/solicitacoes/filtro` | Gestor |
| `PUT` | `/api/solicitacoes/status` | Gestor |

---

## Organização do código

### Backend (`backend/src/main/java/org/boligon/`)

| Pacote | Papel |
|--------|-------|
| `auth` | Login, registro, domínio `Usuario` e repositório |
| `solicitacao` | Entidades, DTOs, repositórios e regras de negócio das solicitações |
| `historico` | Registro e consulta de mudanças de status |
| `enums` | `Categoria`, `Prioridade`, `StatusSolicitacao`, `PerfilUsuario` |
| `shared/model` | Conceitos de composição, ex.: `FilaAtendimento` |
| `config` | Spring Security, CORS e sessão |
| `configbanco` | Usuários padrão e inicialização do banco |
| `exception` | Tratamento global de erros da API |
| `web` | Endpoints auxiliares (enums) |

A camada de serviço concentra validações, geração de protocolo, cálculo de SLA, transições permitidas e registro de auditoria após alteração de status.

### Frontend (`frontend/src/`)

| Pasta | Papel |
|-------|-------|
| `pages/` | Telas (`LoginPage`, `GestorPage`, `NovaSolicitacaoPage`, …) |
| `components/` | Layout, badges, modal, timeline e ícones reutilizáveis |
| `context/` | Estado de autenticação (`AuthContext`) |
| `api/` | Cliente HTTP para a API REST |
| `utils/` | Rótulos e helpers de exibição |

---

## Manutenção de software

A análise estática do projeto foi feita com **SonarCloud**. O relatório resumido (até 4 páginas) está em `manutencao-de-software/Relatorio_Manutencao.pdf`.

---

## Dados locais

Os arquivos do H2 ficam em `backend/data/` (por exemplo `observacao_db.mv.db`). Esse diretório não deve ser versionado no Git; cada clone gera sua própria base ao subir o backend.

---

## Licença e uso

Uso educacional. Ajustem créditos e licença conforme orientação do curso.
