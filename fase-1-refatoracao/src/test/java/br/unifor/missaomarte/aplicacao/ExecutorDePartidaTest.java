package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.EntradaRanking;
import br.unifor.missaomarte.dominio.Ranking;
import br.unifor.missaomarte.dominio.TipoEvento;
import br.unifor.missaomarte.entrada.InterpretadorPorTecla;
import br.unifor.missaomarte.suporte.Cenarios;
import br.unifor.missaomarte.suporte.EntradaRoteirizada;
import br.unifor.missaomarte.suporte.RegistroDeApresentacao;
import br.unifor.missaomarte.suporte.RepositorioEmMemoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * O caso de uso "jogar partida" testado com entrada, tela e repositório falsos: nenhum
 * terminal, nenhum arquivo. Só é possível porque ele depende de interfaces (DIP).
 */
class ExecutorDePartidaTest {
    private static final Clock RELOGIO_FIXO = Clock.fixed(Instant.parse("2026-01-01T12:00:07Z"), ZoneOffset.UTC);

    /** Sequência que vence com a semente 1, nível fácil, mapa 1 (achada por busca e conferida contra o original). */
    private static final String[] PARTIDA_VENCEDORA = {"w", "c", "d", "c", "s", "c", "a", "a", "c", "d"};

    private final RegistroDeApresentacao tela = new RegistroDeApresentacao();

    private ExecutorDePartida executor(RepositorioEmMemoria repo, String... respostas) {
        EntradaRoteirizada entrada = new EntradaRoteirizada(respostas);
        ServicoDeRanking servico = new ServicoDeRanking(entrada, tela, repo);
        return new ExecutorDePartida(entrada, new InterpretadorPorTecla(), tela, servico, new Random(1), RELOGIO_FIXO);
    }

    private static String[] roteiro(String piloto, String dificuldade, String tamanho, String... comandos) {
        List<String> r = new ArrayList<>(List.of(piloto, dificuldade, tamanho, ""));   // "" = Enter para decolar
        r.addAll(List.of(comandos));
        return r.toArray(new String[0]);
    }

    private static Ranking rankingCheioDeRecordes() {
        Ranking r = Ranking.vazio();
        for (int i = 0; i < 5; i++) r = r.comNovaEntrada(Cenarios.entrada("Top" + i, 200 - i));
        return r;
    }

    @Test
    @DisplayName("mapa menor que o mínimo da dificuldade é recusado em vez de travar (corrige o original)")
    void mapaPequenoDemais() {
        executor(new RepositorioEmMemoria(Ranking.vazio()), "Ana", "medio", "1").executar(Ranking.vazio());

        assertEquals(List.of("mapaPequeno:MEDIO:2"), tela.chamadas);
    }

    @Test
    @DisplayName("fim da entrada antes do Enter: a missão não começa")
    void fimDaEntradaAntesDeDecolar() {
        executor(new RepositorioEmMemoria(Ranking.vazio()), "Ana", "facil", "3").executar(Ranking.vazio());

        assertEquals(List.of("inicioMissao:FACIL", "missaoNaoIniciada"), tela.chamadas);
    }

    @Test
    @DisplayName("q aborta a missão e nada é gravado")
    void abortar() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(Ranking.vazio());
        executor(repo, roteiro("Ana", "facil", "3", "q")).executar(Ranking.vazio());

        assertEquals(List.of("inicioMissao:FACIL", "estado", "evento:MISSAO_ABORTADA"), tela.chamadas);
        assertEquals(0, repo.vezesQueSalvou);
    }

    @Test
    @DisplayName("linha em branco com entrada restante apenas pede o comando de novo")
    void linhaEmBranco() {
        executor(new RepositorioEmMemoria(Ranking.vazio()), roteiro("Ana", "facil", "3", "", "q")).executar(Ranking.vazio());

        assertEquals(2, tela.quantas("estado"));
        assertEquals(1, tela.quantas("evento:MISSAO_ABORTADA"));
    }

    @Test
    @DisplayName("linha em branco sem mais entrada aborta a missão")
    void linhaEmBrancoNoFim() {
        executor(new RepositorioEmMemoria(Ranking.vazio()), roteiro("Ana", "facil", "3", "")).executar(Ranking.vazio());

        assertEquals(List.of("inicioMissao:FACIL", "estado", "abortadaPorFimDeEntrada"), tela.chamadas);
    }

    @Test
    @DisplayName("comando inválido avisa e não gasta turno (nada acontece no jogo)")
    void comandoInvalido() {
        executor(new RepositorioEmMemoria(Ranking.vazio()), roteiro("Ana", "facil", "3", "x", "q")).executar(Ranking.vazio());

        assertEquals(List.of("inicioMissao:FACIL", "estado", "comandoInvalido", "estado", "evento:MISSAO_ABORTADA"),
                tela.chamadas);
        assertEquals(30, tela.ultimaPartida.getPontuacao());
        assertEquals(0, tela.ultimaPartida.getMovimentos());
    }

    @Test
    @DisplayName("nome em branco vira \"Piloto Anônimo\"; tamanho 0 vira 5; dificuldade desconhecida vira médio")
    void valoresPadrao() {
        executor(new RepositorioEmMemoria(Ranking.vazio()), roteiro("", "xyz", "0", "q")).executar(Ranking.vazio());

        assertEquals("Piloto Anônimo", tela.ultimaPartida.getPiloto());
        assertEquals(Dificuldade.MEDIO, tela.ultimaPartida.getDificuldade());
        assertEquals(5, tela.ultimaPartida.getLimites().maxX());
        assertEquals(-5, tela.ultimaPartida.getLimites().minY());
    }

    @Test
    @DisplayName("tamanho não numérico avisa e usa 5")
    void tamanhoInvalido() {
        executor(new RepositorioEmMemoria(Ranking.vazio()), roteiro("Ana", "facil", "abc", "q")).executar(Ranking.vazio());

        assertEquals(1, tela.quantas("tamanhoInvalido"));
        assertEquals(5, tela.ultimaPartida.getLimites().maxX());
    }

    @Test
    @DisplayName("vitória mostra estatísticas e grava a pontuação no ranking")
    void vitoriaGravaNoRanking() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(Ranking.vazio());

        executor(repo, roteiro("Ana", "facil", "1", PARTIDA_VENCEDORA)).executar(Ranking.vazio());

        assertTrue(tela.chamadas.contains("evento:" + TipoEvento.MISSAO_CUMPRIDA.name()));
        assertEquals(74, tela.ultimoResumo.pontuacao());
        assertEquals(6, tela.ultimoResumo.movimentos());
        assertEquals(4, tela.ultimoResumo.passageirosResgatados());
        assertTrue(tela.ultimoResumo.lider().isEmpty());
        assertEquals(1, repo.vezesQueSalvou);
        assertEquals(List.of(new EntradaRanking("Ana", 74, Dificuldade.FACIL, 4, "2026-01-01 12:00:07", 0)),
                repo.guardado.getEntradas());
        assertEquals("novoTop5", tela.chamadas.get(tela.chamadas.size() - 1));
    }

    @Test
    @DisplayName("vitória abaixo do Top 5 mostra o recorde a bater e não grava")
    void vitoriaForaDoTop5() {
        Ranking recordes = rankingCheioDeRecordes();
        RepositorioEmMemoria repo = new RepositorioEmMemoria(recordes);

        executor(repo, roteiro("Ana", "facil", "1", PARTIDA_VENCEDORA)).executar(recordes);

        assertEquals(0, repo.vezesQueSalvou);
        assertFalse(tela.chamadas.contains("novoTop5"));
        assertFalse(tela.ultimoResumo.bateuRecorde());
        assertEquals(200, tela.ultimoResumo.recordeAtual());
    }

    @Test
    @DisplayName("falha ao gravar o ranking é informada e a vitória continua valendo")
    void falhaAoGravar() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(Ranking.vazio());
        repo.falharAoSalvarCom = "disco cheio";

        executor(repo, roteiro("Ana", "facil", "1", PARTIDA_VENCEDORA)).executar(Ranking.vazio());

        assertTrue(tela.chamadas.contains("erroAoSalvar:disco cheio"));
        assertEquals(0, repo.vezesQueSalvou);
    }
}
