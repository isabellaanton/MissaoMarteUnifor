# Fase 1 — Refatoração do Missão Marte Unifor

Versão refatorada do jogo de console. A versão inicial (pacote padrão, na raiz do
repositório) permanece intacta para comparação.

## Estrutura

```
fase-1-refatoracao/
├── docs/
│   ├── A1-analise-de-acoplamento.md
│   ├── A2-organizacao-em-pacotes.md
│   └── A3-A4-refatoracao.md
└── src/
    ├── main/java/br/unifor/missaomarte/   (dominio, aplicacao, entrada, apresentacao, infraestrutura, Main)
    └── test/
        ├── java/br/unifor/missaomarte/    (20 classes de teste JUnit 5)
        └── resources/golden/              (57 sessões de referência do jogo original)
```

A organização segue o layout padrão do Gradle (`src/main/java`, `src/test/java`,
`src/test/resources`), para que a migração da issue B2 não precise mover arquivos.

## Requisitos
- Java 17

## Executar o jogo (sem Gradle, enquanto a B2 não entrega)

Dentro de `fase-1-refatoracao`, no PowerShell:

```powershell
$files = Get-ChildItem -Recurse src/main/java -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 --release 17 -d out/main $files
java -cp out/main br.unifor.missaomarte.Main
```

O arquivo `ranking.json` é criado no diretório de onde o jogo for iniciado (igual à versão
original). Controles: `w`, `s`, `a`, `d` movem; `c` embarca; `q` aborta.

## Executar os testes

Os testes são **JUnit 5** e dependem da configuração Gradle das issues **B2** (Gradle
com Wrapper, Java 17) e **B3** (JUnit 5). O `build.gradle` da equipe precisa de:

```groovy
dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.2'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
test {
    useJUnitPlatform()
}
```

e então `./gradlew test` (Windows: `gradlew.bat test`).

Pontos que dependem do Gradle padrão:
- `DependenciasEntreCamadasTest` lê as classes compiladas a partir do local de saída de
  `Main.class` (funciona com a saída padrão `build/classes/java/main`).
- `JogoGoldenMasterTest` lê `src/test/resources/golden` pelo classpath.

## O que os testes cobrem

| Camada | Testes |
|---|---|
| Domínio | `NaveTest`, `MissaoTest`, `InimigoTest`, `PassageiroTest`, `DificuldadeTest`, `LimitesTest`, `GeradorDeMissaoTest`, `PartidaTest`, `RankingTest`, `ResumoPartidaTest` |
| Aplicação (com entrada, tela e repositório falsos) | `ExecutorDePartidaTest`, `SessaoDeJogoTest`, `ServicoDeRankingTest` |
| Adaptadores | `ConsoleEntradaTest`, `InterpretadorPorTeclaTest`, `ConsoleApresentacaoTest`, `MapaTextualTest`, `RankingJsonRepositoryTest` |
| Arquitetura | `DependenciasEntreCamadasTest` (falha se o domínio usar E/S ou depender de outra camada) |
| Comportamento preservado | `JogoGoldenMasterTest` (57 sessões completas comparadas com a saída do jogo original) |

Detalhes e evidências em `docs/A3-A4-refatoracao.md`.
