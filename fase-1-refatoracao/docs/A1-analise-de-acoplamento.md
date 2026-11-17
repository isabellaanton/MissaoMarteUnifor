# A1 — Análise do acoplamento entre as classes (versão original de console)

Escopo: as 11 classes do jogo original (raiz do repositório, pacote padrão). Todas as
referências de linha são ao `Main.java` original (569 linhas).

## 1. Quem cria quem

| Quem cria | O que cria | Onde |
|---|---|---|
| `Main.main` | `Scanner`, `Random` | linhas 27–28 |
| `Main.criarNovaMissao` | `Nave`, `Missao`, `Asteroide`, `Inimigo` | 252–299 |
| `Main.criarPassageiroPolimorfico` | `Professor`, `Engenheiro`, `Astronauta` | 304–312 |
| `Main.jogarPartida` | `RankingEntry` | 188–195 |
| `Main.parseRankingJson` | `RankingEntry` | 543 |
| `Missao`, `Nave`, `Inimigo`, ... | nada além de listas internas | — |

Conclusão: **toda a criação de objetos de jogo está no `Main`**. As classes de domínio não
conhecem umas às outras por construção, só por parâmetro (`colideCom(Nave)`).

## 2. Quem imprime e quem lê

- `System.out`: **68 ocorrências, todas no `Main`**. Nenhuma nas outras 10 classes.
- `Scanner`: só no `Main`.
- Ou seja, o enunciado da issue A3 ("tirar System.out e Scanner das classes de regra")
  descreve um problema que **o domínio original não tem**. O problema real é o inverso:
  **as regras estão dentro do `Main`**, misturadas com E/S.

## 3. Onde estão as regras

| Regra | Onde estava | Linhas |
|---|---|---|
| Pontuação inicial por dificuldade | `Main.definirPontuacaoInicial` | 244–250 |
| Quantidade de elementos por dificuldade | `Main.criarNovaMissao` | 256–268 |
| Custo de movimento (-1 ponto) | dentro do `while` de `jogarPartida` | 148 |
| Bônus de embarque | idem | 139–140 |
| Ordem do turno (ação, inimigos, colisão, pontuação, vitória) | idem | 127–208 |
| Perda de vida / game over | idem | 159–167 |
| Pontuação zerada | idem | 170–173 |
| Condição de vitória (todos a bordo **e** em (0,0)) | idem | 176–208 |
| Tempo de jogo | idem | 109, 178–179 |
| Regra do Top 5 (`isTopScore`, ordenar, limitar a 5) | `Main` | 187–201, 434–439 |
| Símbolo de cada elemento no mapa | `Main.desenharMapa` | 345–379 |

`jogarPartida` tem **130 linhas** (81–210) e faz, ao mesmo tempo: perguntar nome, nível e
tamanho; montar a missão; desenhar; ler comando; interpretar tecla; aplicar regras;
imprimir mensagens; calcular tempo; gravar ranking.

## 4. Responsabilidades do `Main`

Contei **oito responsabilidades distintas** em uma só classe (569 linhas, 19 métodos
estáticos, 1 classe aninhada):

1. Laço do menu (26–62)
2. Conversa com o jogador / leitura de linha (81–101, 228–242, 394–402)
3. Regras da partida (111–209)
4. Fábrica de missões (252–326)
5. Renderização do mapa e de todas as mensagens (64–79, 212–226, 328–392, 404–416)
6. Regras do ranking (187–201, 434–439)
7. Persistência em arquivo (418–481)
8. Leitor manual de JSON (483–550) + modelo `RankingEntry` (552–568)

## 5. Problemas de acoplamento e de projeto encontrados

| # | Problema | Evidência | Princípio violado |
|---|---|---|---|
| P1 | `Main` é uma "classe Deus" | seção 4 | SRP |
| P2 | Regras só existem dentro de método `static` com `Scanner` e `System.out`: impossível testar sem terminal | `jogarPartida` | SRP, testabilidade |
| P3 | `Nave.moverComLimites(char 'w'/'s'/'a'/'d')`: o teclado vaza para dentro do domínio | `Nave` | SRP, Protected Variations |
| P4 | Limites do mapa passados como 4 inteiros soltos (`minX, maxX, minY, maxY`) em 4 métodos | `Missao.moverInimigos`, `Inimigo.mover`, `Nave.moverComLimites`, `Main.*` | Data Clumps / Value Object |
| P5 | Configuração de dificuldade duplicada em dois `switch`/`if`; novo nível exige editar o fluxo do jogo | 244–250 e 256–268 | OCP |
| P6 | `desenharMapa` descobre o símbolo com `instanceof` nas subclasses; `Passageiro.tipo` é `String` livre | 351–357 | OCP, tipagem fraca |
| P7 | `colideCom` duplicado em `Asteroide` e `Inimigo`; comparação de posição `x == x && y == y` repetida em vários lugares | `Asteroide`, `Inimigo`, `Missao`, `Main` | DRY / Polimorfismo |
| P8 | Três laços quase idênticos de sorteio de posição (passageiros, asteroides, inimigos) | 271–299 | DRY |
| P9 | `Missao`, `Nave` expõem `List` mutável (`getPassageiros()`) | `Missao`, `Nave` | Encapsulamento |
| P10 | Persistência **imprime** mensagem de erro em vez de sinalizar o erro | `saveRanking` (479) | SRP |
| P11 | Tempo e semente acessados direto (`System.currentTimeMillis`, `new Random()`): partida não reproduzível | 28, 109, 178, 193 | DIP |
| P12 | Leitor de JSON manual e frágil: nome com vírgula, `:` ou `}` perde a entrada; `\` não é escapado | `parseRankingJson` | Robustez |
| P13 | `Passageiro` concreta com pontuação "default" 10 que nunca deveria ser usada | `Passageiro` | LSP / modelagem |
| P14 | Métodos `moveUp/moveDown/moveLeft/moveRight` em `Nave` nunca usados | `Nave` | Código morto |

## 6. Defeitos de comportamento achados (além do acoplamento)

| # | Defeito | Como reproduzir | Situação na refatoração |
|---|---|---|---|
| D1 | **Laço infinito** na geração do mapa quando ele não comporta os elementos | Nível Médio (9 elementos) ou Difícil (11) com mapa de tamanho 1 (8 células livres) | **Corrigido**: mapa recusado com mensagem. Mudança intencional, ver `A3-A4-refatoracao.md` |
| D2 | `LocalDateTime.now().toString().substring(0, 19)` estoura `StringIndexOutOfBounds` se segundos e nanos forem zero | Relógio fixo em hh:mm:00.000 | **Corrigido** com `DateTimeFormatter` (saída idêntica nos casos normais) |
| D3 | Nome de piloto com vírgula/dois-pontos é perdido ao recarregar o ranking | Nome `Silva, Jr` | **Preservado e documentado** (teste de limitação conhecida); correção natural na fase do servidor |

## 7. Fluxo de uma partida (como está no original)

```
main ──► menu ──(1)──► jogarPartida
                        ├─ lerLinha ×3 (nome, dificuldade, tamanho)
                        ├─ criarNovaMissao ──► new Nave/Missao/Asteroide/Inimigo/Passageiro*
                        └─ while (ativa)
                             ├─ desenharMapa + println do status
                             ├─ lerLinha (comando)
                             ├─ q → aborta | c → embarca | wasd → move, score--, movimentos++
                             ├─ missao.moverInimigos(random)
                             ├─ colisão → perderVida → game over?
                             ├─ score <= 0 → derrota
                             └─ todos a bordo? em (0,0)? → vitória → ranking → saveRanking
```

## 8. Proposta resultante

A organização proposta (A2) e a refatoração (A3/A4) resolvem P1–P14 e D1–D2. A
tabela problema → solução → princípio está em `A3-A4-refatoracao.md`.
