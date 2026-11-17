# A3 e A4 — Refatoração do domínio e adaptadores de console

## 1. O que as issues pediam e o que o código realmente precisava

- **A3** pedia tirar `System.out`/`Scanner` das classes de regra. Na análise (A1) vimos que
  as classes de domínio **já eram livres de E/S**; as regras estavam dentro do `Main`.
  Então A3 virou: **extrair as regras do `Main` para classes de domínio testáveis**, e
  limpar as classes de domínio dos vazamentos que existiam (teclado em `Nave`, limites
  soltos, `instanceof`, listas mutáveis).
- **A4** pedia entrada e apresentação do console como adaptadores. Foi feito com portas
  (interfaces) na camada de aplicação e implementações de console.

## 2. Princípios aplicados: onde aparecem e por que ajudaram

### SOLID

| Princípio | Onde aparece | Problema que resolveu |
|---|---|---|
| **S** — Responsabilidade única | `Main` (569 linhas, 8 responsabilidades) foi dividido: `Partida` (regras), `GeradorDeMissao` (criação), `Ranking` (regra Top 5), `RankingJsonRepository` (arquivo), `ConsoleApresentacao`/`MapaTextual` (texto), `ConsoleEntrada` (teclado). O novo `Main` tem 46 linhas e só liga as peças | P1, P2, P10 |
| **O** — Aberto/fechado | `Dificuldade` guarda os parâmetros de cada nível: criar um nível novo é adicionar uma constante, sem editar o fluxo. `TipoPassageiro` + `MapaTextual.simbolo` substituem o `instanceof` | P5, P6 |
| **L** — Substituição de Liskov | `Passageiro` virou abstrata com `getPontuacao()` abstrato: não existe mais o "passageiro genérico de 10 pontos". Qualquer subclasse pode ser usada onde `Passageiro` é esperado | P13 |
| **I** — Segregação de interfaces | A apresentação foi dividida em três portas pequenas (`ApresentacaoMenu`, `ApresentacaoPartida`, `ApresentacaoRanking`); cada caso de uso depende só do que usa | — |
| **D** — Inversão de dependência | Casos de uso dependem de `EntradaDoJogador`, `RepositorioRanking`, `Apresentacao*`, `InterpretadorDeComandos` (interfaces da camada de aplicação). `Partida` recebe `Random` e `Clock` por construtor | P11 |

### GRASP

| Padrão | Onde aparece |
|---|---|
| **Information Expert** | `Missao` responde "há colisão?", "célula ocupada?", "quem está nesta célula?" porque tem os dados. `Ranking` decide se uma pontuação qualifica |
| **Creator** | `GeradorDeMissao` cria a `Missao` e seus elementos (agrega/contém, tem os dados de inicialização) |
| **Controller** | `SessaoDeJogo` e `ExecutorDePartida` recebem as ações do jogador e as coordenam, sem conter regra |
| **Low Coupling / High Cohesion** | O domínio não importa nada de fora; cada classe tem um motivo para mudar. Verificado por `DependenciasEntreCamadasTest` |
| **Polymorphism** | `Obstaculo.colideCom` (um só lugar para `Asteroide` e `Inimigo`); `Passageiro.getPontuacao()` |
| **Pure Fabrication** | `GeradorDeMissao`, `ServicoDeRanking`, `MapaTextual`: classes que não existem no mundo do jogo, criadas para manter coesão |
| **Protected Variations** | `Comando`/`Direcao` isolam o domínio do dispositivo de entrada; `Evento` isola as regras do modo de exibição |
| **Indirection** | As portas entre `aplicacao` e os adaptadores |

### Outros padrões usados
- **Value Object**: `Limites`, `EntradaRanking`, `Evento`, `ResumoPartida` (records imutáveis).
- **Repository**: `RepositorioRanking`.
- **Ports & Adapters (arquitetura hexagonal)**: organização geral.
- **Composition Root**: `Main.criarSessao`.

## 3. Como as regras agora se comunicam com a tela

A `Partida` recebe um `Comando` e devolve uma lista de `Evento` (`PASSAGEIRO_EMBARCADO`,
`COLISAO`, `NAVE_DESTRUIDA`, `MISSAO_CUMPRIDA`...). Ela **descreve o que aconteceu**;
quem decide **como mostrar** é a apresentação. Por isso a mesma `Partida` servirá ao
console hoje e ao libGDX/Android depois.

```java
List<Evento> eventos = partida.processar(Comando.MOVER_DIREITA);
eventos.forEach(apresentacao::exibirEvento);   // ConsoleApresentacao hoje, tela libGDX amanhã
```

## 4. Evidência de que o comportamento foi preservado

### 4.1 Comparação com o jogo original (408 cenários)
Foi criada uma cópia do `Main` original **fora do repositório** com apenas três pontos
fixados para tornar a execução reproduzível:

| Original | Cópia para comparação |
|---|---|
| `new Random()` | `new Random(<semente>)` |
| `System.currentTimeMillis()` (2×) | `0L` |
| `LocalDateTime.now()` | `LocalDateTime.of(2026, 1, 1, 12, 0, 7)` |

O resto é código original sem alteração. A versão refatorada recebe a mesma semente e
um relógio fixo equivalente. Foram executados **408 cenários** em cada versão e
comparados **byte a byte** a saída padrão e o `ranking.json` resultante:

- 24 fluxos de menu e de fim de entrada (EOF) em cada ponto do fluxo;
- 330 partidas aleatórias reprodutíveis (movimentos, colisões, game over, pontuação zero,
  comandos inválidos, abortar, entrada cortada no meio, tamanhos inválidos);
- 54 cenários de vitória com ranking (vazio, cheio de pontuações altas e baixas, empates,
  nome com aspas/vírgula/dois-pontos, arquivo corrompido, entradas incompletas, mais de
  5 entradas, jogar de novo e resetar depois da vitória).

**Resultado: 460 arquivos de saída, nenhuma diferença.** As sequências vencedoras foram
achadas por busca usando as classes novas, mas a validação foi feita executando o jogo
**original** com essas mesmas entradas.

### 4.2 Teste de caracterização no repositório
`JogoGoldenMasterTest` guarda 57 desses cenários em `src/test/resources/golden/`, com
a saída **produzida pelo jogo original**. Ele roda a versão refatorada e exige igualdade.
Se alguém mudar uma mensagem, uma regra ou a ordem das chamadas ao `Random`, ele falha.

### 4.3 Verificação de que os testes pegam defeitos
Foram injetados 5 defeitos deliberados (um por vez) e todos foram detectados:
movimento custando 2 pontos, ranking com 4 vagas, sorteio de `y` antes de `x`,
`System.out` dentro do domínio, e remoção da checagem de colisão.

## 5. Mudanças intencionais de comportamento

| # | Mudança | Motivo |
|---|---|---|
| M1 | Mapa pequeno demais para a dificuldade (ex.: tamanho 1 no Médio/Difícil) agora mostra "Mapa pequeno demais para a dificuldade X. Use o tamanho mínimo N." e volta ao menu | O original **travava em laço infinito** (D1) |
| M2 | Data/hora do ranking formatada com `DateTimeFormatter("yyyy-MM-dd HH:mm:ss")` | O original usava `toString().substring(0,19)`, que falha quando os segundos são zero (D2). Saída idêntica nos demais casos |
| M3 | API de domínio: `Nave.mover(Direcao, Limites)` no lugar de `moverComLimites(char, ...)`; `Missao.moverInimigos(Random, Limites)`; coleções somente leitura; removidos `moveUp/Down/Left/Right` (não usados) | Remover vazamento do teclado e quatro inteiros soltos (P3, P4, P9, P14) |
| M4 | `Passageiro` é abstrata e o tipo é `TipoPassageiro` (enum) em vez de `String` | P6, P13 |

Nenhuma delas afeta o jogo em condições normais; M1 só aparece em entrada que antes
travava o programa. M1 e M2 têm testes próprios.

## 6. Limitações conhecidas (não corrigidas nesta fase)

- **Parser JSON manual** (herdado): nome de piloto com vírgula, dois-pontos ou `}` é
  perdido ao recarregar o ranking; barra invertida não é escapada. Está documentado e
  coberto por um teste de "limitação conhecida" (`RankingJsonRepositoryTest`). A correção
  natural é adotar uma biblioteca JSON na fase do servidor.
- **Mensagens de interface em português dentro de `Dificuldade.toString()`**: o rótulo
  de exibição ainda mora no enum de domínio. Foi mantido para preservar a saída; mover
  para a apresentação é uma melhoria futura (exigiria alterar o formato do ranking
  exibido).
- **Cobertura de testes**: não foi medida com ferramenta (JaCoCo) porque ainda não há
  Gradle no projeto; ver o README desta pasta.

## 7. Como executar os testes

Ver `README.md` desta pasta. Resumo: os testes usam **JUnit 5** e precisam do Gradle
das issues B2/B3.
