# A2 — Organização em pacotes

Pacote raiz: `br.unifor.missaomarte`. Cada camada tem uma responsabilidade e uma
direção de dependência permitida.

```
br.unifor.missaomarte
├── Main                      raiz de composição: liga as peças, sem regra nem E/S
├── dominio/                  regras do jogo, puras, sem E/S
├── aplicacao/                casos de uso + portas (interfaces)
├── entrada/                  adaptadores de entrada (console)
├── apresentacao/             adaptadores de saída (console)
└── infraestrutura/           adaptadores de persistência (arquivo JSON)
```

## Regra de dependência

```
        entrada ─────┐
   apresentacao ─────┼──►  aplicacao  ──►  dominio
 infraestrutura ─────┘     (portas)
              ▲
              └── Main (conhece todos, para montar)
```

- `dominio` não importa nada das outras camadas nem `Scanner`/`System`/`java.io`.
- `aplicacao` define as **portas** (interfaces) e só conhece o `dominio`.
- Os adaptadores (`entrada`, `apresentacao`, `infraestrutura`) **implementam** as portas.
  A seta de dependência aponta para dentro: é a Inversão de Dependência (DIP).
- Isso é verificado automaticamente por `DependenciasEntreCamadasTest`, que lê o
  bytecode e falha se alguém violar a regra.

## O que fica em cada pacote

### `dominio` (23 classes)
| Grupo | Classes |
|---|---|
| Elementos do mapa | `Nave`, `Passageiro` (abstrata), `Professor`, `Engenheiro`, `Astronauta`, `Asteroide`, `Inimigo`, `Missao` |
| Conceitos de apoio | `Posicionavel`, `Obstaculo`, `Limites`, `Direcao`, `TipoPassageiro`, `Dificuldade` |
| Regras de partida | `Partida`, `GeradorDeMissao`, `Comando`, `Evento`, `TipoEvento`, `EstadoPartida`, `ResumoPartida` |
| Regras de ranking | `Ranking`, `EntradaRanking` |

### `aplicacao` (10 classes)
| Tipo | Classes |
|---|---|
| Casos de uso | `SessaoDeJogo` (menu), `ExecutorDePartida` (jogar), `ServicoDeRanking` |
| Portas de entrada | `EntradaDoJogador`, `InterpretadorDeComandos` |
| Portas de saída | `ApresentacaoMenu`, `ApresentacaoPartida`, `ApresentacaoRanking` |
| Porta de persistência | `RepositorioRanking`, `FalhaDePersistencia` |

### Adaptadores
| Pacote | Classes | Único lugar que conhece |
|---|---|---|
| `entrada` | `ConsoleEntrada`, `InterpretadorPorTecla` | `Scanner`; teclas w/s/a/d/c/q |
| `apresentacao` | `ConsoleApresentacao`, `MapaTextual` | `System.out`; textos e símbolos do mapa |
| `infraestrutura` | `RankingJsonRepository` | arquivo `ranking.json`; formato JSON |

## Por que quatro/cinco pacotes e não três

O enunciado pede domínio, aplicação, entrada e apresentação. Acrescentei
`infraestrutura` porque a persistência não é nem entrada nem apresentação: é um terceiro
tipo de adaptador, e na Sprint 3 ela será substituída/complementada pela API REST. Ter um
pacote próprio deixa claro onde essa troca acontece.

## Como esta organização prepara as próximas sprints

| Sprint | O que muda | O que **não** muda |
|---|---|---|
| 2 (libGDX) | Novo adaptador de entrada (teclado/mouse) e de saída (telas) implementando as mesmas portas | `dominio` inteiro e `aplicacao` |
| 3 (servidor) | Novo `RepositorioRanking` que chama a API REST | Regras do Top 5 em `Ranking` |
| 4 (Android) | Novo adaptador de entrada por toque produzindo `Comando` | `Partida` e todas as regras |

## Mapeamento dos pacotes às perguntas da apresentação

- *Onde ficam as regras de negócio?* `dominio` (principalmente `Partida` e `Ranking`).
- *Como os testes demonstram que elas não dependem da interface?* `PartidaTest` joga
  partidas inteiras sem Scanner nem System.out; `DependenciasEntreCamadasTest` falha se o
  domínio referenciar E/S.
- *Como as responsabilidades mudaram entre console e gráfica?* Só os adaptadores trocam.
