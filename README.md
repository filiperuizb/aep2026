# ObservaAção — Etapa 1 (beta em linha de comando)

Sistema em **Java**, sem framework web, para registro e acompanhamento de solicitações à prefeitura (iluminação, buracos, zeladoria, saúde, etc.). Foi pensado no contexto da govtech fictícia **ObservaAção**, com foco em **transparência**, **rastreio por protocolo** e **tratamento organizado** das demandas pelo gestor.

A motivação do problema liga-se à **ODS 16** (paz, justiça e instituições eficazes): reduzir a opacidade no atendimento, dar previsibilidade de prazos (SLA) e registrar movimentações com responsável — em especial para quem depende do serviço público e não tem “facilitador” informal.

---

## Requisitos

| Item | Versão / observação |
|------|---------------------|
| JDK | 17 ou superior |
| Maven | 3.6+ |
| Banco | H2 em arquivo local (`./data/observacao_db`), criado na primeira execução |

---

## Como compilar e executar

No diretório do projeto:

```bash
mvn compile exec:java
```

O `Main` inicializa o banco, abre a tela de login e, em seguida, o menu conforme o perfil do usuário.

**Conta de gestor (dados de desenvolvimento):**

| Campo | Valor |
|-------|--------|
| E-mail | `admin@admin.com` |
| Senha | `123` |

É possível **registrar** novos cidadãos pelo menu inicial ou entrar **anônimo** (sem cadastro), com acompanhamento apenas pelo **protocolo** gerado ao criar a solicitação.

---

## O que a beta faz (fluxo mínimo)

- **Cidadão / anônimo:** abrir solicitação (categoria, descrição, localização, bairro, prioridade, anexo opcional como referência de arquivo, opção de anonimato quando logado como cidadão).
- **Qualquer um com o protocolo:** consultar situação, prazo de SLA, indicação de atraso, justificativa quando existir, e histórico de mudanças com **nome do responsável**.
- **Gestor:** ver fila ordenada por prioridade, filtrar por critérios isolados ou combinados, avançar o status **seguindo a sequência** da fila (aberto → triagem → em execução → resolvido → encerrado), com **comentário obrigatório** e **justificativa de atraso** quando o prazo já tiver passado.

---

## Organização do código (visão POO)

| Pacote / pasta | Papel |
|----------------|--------|
| `entity` | Objetos de domínio (`Solicitacao`, `Usuario`, `HistoricoStatus`, …) |
| `enums` | `Categoria`, `Prioridade`, `StatusSolicitacao`, `PerfilUsuario` |
| `dto` | Objetos de transferência para operações pontuais (`HistoricoStatusDTO`, `FiltroSolicitacaoDTO`, …) |
| `model` | Conceitos de composição, ex.: `FilaAtendimento` |
| `repository` | Acesso ao H2 (SQL) |
| `service` | Regras de negócio (`SolicitacaoService`, `HistoricoStatusService`, autenticação) |
| `ui` | Menus e leitura pelo `Scanner` |
| `configbanco` | Conexão e script inicial das tabelas |

A camada de serviço concentra validações, geração de protocolo, cálculo de SLA, transições permitidas e registro de auditoria após alteração de status.

---

## Dados locais

Os arquivos do H2 ficam em `./data/` (por exemplo `observacao_db.mv.db`). Não versionar esse diretório no Git se a equipe quiser evitar conflitos de banco entre máquinas; cada clone pode gerar sua própria base ao rodar o `Main`.

---

## Autores e entregáveis da etapa

Trabalho acadêmico — **1º bimestre**. Além deste repositório, a entrega prevê documentação de **IHC (perfis e personas)**, **relatório de Clean Code (três funções)** e **vídeo** de até cinco minutos, conforme critério da disciplina.

O texto base para o relatório de Clean Code está em [`RELATORIO_CLEAN_CODE_ETAPA1.md`](RELATORIO_CLEAN_CODE_ETAPA1.md) (pode ser copiado para Word e ajustado).

---

## Licença e uso

Uso educacional. Ajustem créditos e licença conforme orientação do curso.
