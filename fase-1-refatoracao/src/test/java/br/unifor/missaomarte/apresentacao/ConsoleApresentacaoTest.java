package br.unifor.missaomarte.apresentacao;

import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.EntradaRanking;
import br.unifor.missaomarte.dominio.Evento;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Nave;
import br.unifor.missaomarte.dominio.Partida;
import br.unifor.missaomarte.dominio.Professor;
import br.unifor.missaomarte.dominio.Ranking;
import br.unifor.missaomarte.dominio.ResumoPartida;
import br.unifor.missaomarte.dominio.TipoEvento;
import br.unifor.missaomarte.suporte.Cenarios;
import br.unifor.missaomarte.suporte.RelogioManual;
import br.unifor.missaomarte.suporte.Textos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Todo o texto do jogo passa por aqui; as mensagens são as da versão original. */
class ConsoleApresentacaoTest {
    private static final String SEPARADOR = "================================================================";

    private ByteArrayOutputStream bytes;
    private ConsoleApresentacao tela;

    @BeforeEach
    void criarTela() throws Exception {
        bytes = new ByteArrayOutputStream();
        tela = new ConsoleApresentacao(new PrintStream(bytes, true, "UTF-8"));
    }

    private String saida() throws Exception {
        return Textos.normalizar(bytes.toString("UTF-8"));
    }

    private String eventoComoTexto(Evento evento) throws Exception {
        tela.exibirEvento(evento);
        return saida();
    }

    @Test
    @DisplayName("boas-vindas")
    void boasVindas() throws Exception {
        tela.exibirBoasVindas();
        assertEquals(SEPARADOR + "\n"
                + "             MISSÃO MARTE UNIFOR - VERSÃO 10.0                  \n"
                + SEPARADOR + "\n"
                + "  Pilote sua nave, salve os passageiros e desvie dos perigos!   \n"
                + SEPARADOR + "\n", saida());
    }

    @Test
    @DisplayName("menu principal")
    void menu() throws Exception {
        tela.exibirMenu();
        assertEquals("\n--- MENU PRINCIPAL ---\n"
                + "1. Iniciar Nova Missão\n"
                + "2. Visualizar Ranking Top 5\n"
                + "3. Resetar Histórico de Ranking\n"
                + "4. Sair do Jogo\n"
                + "----------------------\n", saida());
    }

    @Test
    @DisplayName("mensagens do menu")
    void mensagensDoMenu() throws Exception {
        tela.exibirOpcaoInvalida();
        tela.exibirDespedida();
        tela.exibirEntradaEncerrada();
        assertEquals("Opção inválida. Tente novamente.\n"
                + "\nObrigado por jogar a Missão Marte Unifor!\n"
                + "\nEntrada encerrada. Obrigado por jogar!\n", saida());
    }

    @Test
    @DisplayName("eventos de turno")
    void eventosDeTurno() throws Exception {
        assertEquals("Passageiro Ast. Maria embarcado com sucesso! +20 pontos!\n",
                eventoComoTexto(Evento.passageiroEmbarcado("Ast. Maria", 20)));
        bytes.reset();
        assertEquals("Nenhum passageiro nesta posição.\n",
                eventoComoTexto(Evento.semDetalhe(TipoEvento.SEM_PASSAGEIRO_NA_POSICAO)));
        bytes.reset();
        assertEquals("Nave cheia! Não há espaço para mais passageiros.\n",
                eventoComoTexto(Evento.semDetalhe(TipoEvento.NAVE_CHEIA)));
        bytes.reset();
        assertEquals("Missão abortada pelo piloto.\n",
                eventoComoTexto(Evento.semDetalhe(TipoEvento.MISSAO_ABORTADA)));
        bytes.reset();
        assertEquals("Alerta! Colisão detectada! Vidas restantes: 2\n", eventoComoTexto(Evento.colisao(2)));
        bytes.reset();
        assertEquals("GAME OVER! A nave foi destruída.\n",
                eventoComoTexto(Evento.semDetalhe(TipoEvento.NAVE_DESTRUIDA)));
        bytes.reset();
        assertEquals("Combustível/Pontuação zerada! Missão perdida.\n",
                eventoComoTexto(Evento.semDetalhe(TipoEvento.PONTUACAO_ZERADA)));
        bytes.reset();
        assertEquals("✨ ALERTA: Todos os passageiros resgatados! Retorne para a Plataforma de Pouso 'L' em (0,0) para completar a missão.\n",
                eventoComoTexto(Evento.semDetalhe(TipoEvento.RETORNE_A_PLATAFORMA)));
    }

    @Test
    @DisplayName("missão cumprida mostra o banner de decolagem")
    void missaoCumprida() throws Exception {
        tela.exibirEvento(Evento.semDetalhe(TipoEvento.MISSAO_CUMPRIDA));
        assertEquals("\n" + SEPARADOR + "\n"
                + "🚀 DECOLAGEM AUTORIZADA! Nave acoplada à plataforma em (0,0).\n"
                + "Retornando à órbita marciana com todos os passageiros. Missão cumprida!\n"
                + SEPARADOR + "\n", saida());
    }

    @Test
    @DisplayName("estatísticas sem ranking anterior")
    void estatisticasSemRecorde() throws Exception {
        tela.exibirEstatisticas(new ResumoPartida(74, 6, 3, 4, Optional.empty()));
        assertEquals("Estatísticas da Partida:\n"
                + " - Pontuação Final: 74 pontos\n"
                + " - Movimentos Efetuados: 6\n"
                + " - Tempo de Jogo: 3 segundos\n"
                + " - Passageiros Resgatados: 4\n"
                + SEPARADOR + "\n", saida());
    }

    @Test
    @DisplayName("estatísticas quando bate o recorde")
    void estatisticasNovoRecorde() throws Exception {
        tela.exibirEstatisticas(new ResumoPartida(74, 6, 3, 4, Optional.of(Cenarios.entrada("Lider", 50))));
        assertTrue(saida().contains("🏆 Novo recorde absoluto do sistema!\n"));
    }

    @Test
    @DisplayName("estatísticas quando não bate o recorde mostram quem detém o recorde")
    void estatisticasRecordeABater() throws Exception {
        tela.exibirEstatisticas(new ResumoPartida(40, 6, 3, 4, Optional.of(Cenarios.entrada("Lider", 50))));
        assertTrue(saida().contains(" - Recorde atual a ser batido: 50 pontos (Piloto: Lider)\n"));
    }

    @Test
    @DisplayName("ranking vazio")
    void rankingVazio() throws Exception {
        tela.exibirRanking(Ranking.vazio());
        assertEquals("\n====== RANKING TOP 5 PILOTOS ======\n"
                + " - Nenhum registro encontrado. Seja o primeiro a jogar!\n"
                + "===================================\n", saida());
    }

    @Test
    @DisplayName("ranking com entradas numeradas")
    void rankingComEntradas() throws Exception {
        Ranking r = new Ranking(List.of(
                new EntradaRanking("Ana", 74, Dificuldade.FACIL, 4, "2026-01-01 12:00:07", 3),
                new EntradaRanking("Bia", 60, Dificuldade.DIFICIL, 5, "2025-12-01 10:00:00", 12)));
        tela.exibirRanking(r);
        assertEquals("\n====== RANKING TOP 5 PILOTOS ======\n"
                + "1. Ana - 74 pts | Dificuldade: Fácil | Coletados: 4 | Tempo: 3s | 2026-01-01 12:00:07\n"
                + "2. Bia - 60 pts | Dificuldade: Difícil | Coletados: 5 | Tempo: 12s | 2025-12-01 10:00:00\n"
                + "===================================\n", saida());
    }

    @Test
    @DisplayName("mensagens de ranking")
    void mensagensDeRanking() throws Exception {
        tela.exibirRankingResetado();
        tela.exibirErroAoResetar("sem permissão");
        tela.exibirResetCancelado();
        tela.exibirErroAoSalvar("disco cheio");
        tela.exibirNovoTop5();
        assertEquals("Ranking resetado com sucesso!\n"
                + "Erro ao resetar ranking: sem permissão\n"
                + "Operação cancelada.\n"
                + "Não foi possível salvar o ranking: disco cheio\n"
                + "Parabéns! Você entrou para o Top 5 de pilotos!\n", saida());
    }

    @Test
    @DisplayName("mensagens de início de missão e de entrada inválida")
    void mensagensDePartida() throws Exception {
        tela.exibirTamanhoDeMapaInvalido();
        tela.exibirInicioDaMissao(Dificuldade.DIFICIL);
        tela.exibirMissaoNaoIniciada();
        tela.exibirComandoInvalido();
        tela.exibirMissaoAbortadaPorFimDeEntrada();
        tela.exibirMapaPequenoDemais(Dificuldade.MEDIO, 2);
        assertEquals("Entrada inválida, usando tamanho padrão (5).\n"
                + "\nIniciando missão na dificuldade Difícil...\n"
                + "Pressione Enter para decolar!\n"
                + "Entrada encerrada. Missão não iniciada.\n"
                + "Comando inválido.\n"
                + "Entrada encerrada. Missão abortada.\n"
                + "Mapa pequeno demais para a dificuldade Médio. Use o tamanho mínimo 2.\n", saida());
    }

    @Test
    @DisplayName("estado da partida: mapa seguido da linha de status")
    void estadoDaPartida() throws Exception {
        Missao missao = new Missao(new Nave("T-1", 5));
        missao.addPassageiro(new Professor("Dr. Silva", 1, 1));
        Partida partida = new Partida("Ana", Dificuldade.FACIL, missao, Cenarios.MAPA_3X3,
                new Random(1), new RelogioManual(0));

        tela.exibirEstado(partida);

        String saida = saida();
        assertTrue(saida.startsWith("\nMapa da Missão (Pontos: 30) - Piloto: Ana\n"));
        assertTrue(saida.endsWith("Nave em (0,0) | Pontos: 30 | Vidas: 3 | A bordo: 0/5 | Restantes: 1\n"));
    }
}
