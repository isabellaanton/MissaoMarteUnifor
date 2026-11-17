package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Ranking;
import br.unifor.missaomarte.entrada.InterpretadorPorTecla;
import br.unifor.missaomarte.suporte.Cenarios;
import br.unifor.missaomarte.suporte.EntradaRoteirizada;
import br.unifor.missaomarte.suporte.RegistroDeApresentacao;
import br.unifor.missaomarte.suporte.RepositorioEmMemoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessaoDeJogoTest {
    private final RegistroDeApresentacao tela = new RegistroDeApresentacao();

    private SessaoDeJogo sessao(RepositorioEmMemoria repo, String... respostas) {
        EntradaRoteirizada entrada = new EntradaRoteirizada(respostas);
        ServicoDeRanking servico = new ServicoDeRanking(entrada, tela, repo);
        ExecutorDePartida executor = new ExecutorDePartida(
                entrada, new InterpretadorPorTecla(), tela, servico, new Random(1), Clock.systemUTC());
        return new SessaoDeJogo(entrada, tela, executor, servico);
    }

    private static Ranking rankingComUmaEntrada() {
        return Ranking.vazio().comNovaEntrada(Cenarios.entrada("Ana", 50));
    }

    @Test
    @DisplayName("opção 4 despede-se e encerra")
    void sair() {
        sessao(new RepositorioEmMemoria(Ranking.vazio()), "4").executar();
        assertEquals(List.of("boasVindas", "menu", "despedida"), tela.chamadas);
    }

    @Test
    @DisplayName("sem entrada nenhuma encerra sem nem mostrar o menu")
    void fimDaEntradaImediato() {
        sessao(new RepositorioEmMemoria(Ranking.vazio())).executar();
        assertEquals(List.of("boasVindas", "entradaEncerrada"), tela.chamadas);
    }

    @Test
    @DisplayName("opção desconhecida avisa e volta ao menu")
    void opcaoInvalida() {
        sessao(new RepositorioEmMemoria(Ranking.vazio()), "9", "4").executar();
        assertEquals(List.of("boasVindas", "menu", "opcaoInvalida", "menu", "despedida"), tela.chamadas);
    }

    @Test
    @DisplayName("opção 2 mostra o ranking carregado")
    void verRanking() {
        Ranking salvo = rankingComUmaEntrada();
        sessao(new RepositorioEmMemoria(salvo), "2", "4").executar();
        assertEquals(1, tela.quantas("ranking"));
        assertEquals(salvo.getEntradas(), tela.ultimoRankingExibido.getEntradas());
    }

    @Test
    @DisplayName("opção 3 confirmada limpa o ranking exibido a seguir")
    void resetarEVerRanking() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(rankingComUmaEntrada());
        sessao(repo, "3", "s", "2", "4").executar();
        assertTrue(tela.ultimoRankingExibido.estaVazio());
        assertEquals(1, repo.vezesQueLimpou);
    }

    @Test
    @DisplayName("opção 3 cancelada mantém o ranking")
    void resetarCancelado() {
        RepositorioEmMemoria repo = new RepositorioEmMemoria(rankingComUmaEntrada());
        sessao(repo, "3", "n", "2", "4").executar();
        assertEquals(1, tela.ultimoRankingExibido.getEntradas().size());
        assertEquals(0, repo.vezesQueLimpou);
    }

    @Test
    @DisplayName("opção 1 joga uma partida e volta ao menu")
    void jogarEVoltarAoMenu() {
        sessao(new RepositorioEmMemoria(Ranking.vazio()), "1", "Ana", "facil", "3", "", "q", "4").executar();
        assertEquals(2, tela.quantas("menu"));
        assertEquals(1, tela.quantas("evento:MISSAO_ABORTADA"));
        assertEquals("despedida", tela.chamadas.get(tela.chamadas.size() - 1));
    }
}
