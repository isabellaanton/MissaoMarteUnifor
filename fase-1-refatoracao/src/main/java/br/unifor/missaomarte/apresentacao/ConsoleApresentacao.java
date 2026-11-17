package br.unifor.missaomarte.apresentacao;

import br.unifor.missaomarte.aplicacao.ApresentacaoMenu;
import br.unifor.missaomarte.aplicacao.ApresentacaoPartida;
import br.unifor.missaomarte.aplicacao.ApresentacaoRanking;
import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.EntradaRanking;
import br.unifor.missaomarte.dominio.Evento;
import br.unifor.missaomarte.dominio.Nave;
import br.unifor.missaomarte.dominio.Partida;
import br.unifor.missaomarte.dominio.Ranking;
import br.unifor.missaomarte.dominio.ResumoPartida;

import java.io.PrintStream;

/**
 * Adaptador de saída: todo texto do jogo está aqui e em {@link MapaTextual}. Implementa
 * as três portas de apresentação; uma tela libGDX implementaria as mesmas interfaces.
 */
public class ConsoleApresentacao implements ApresentacaoMenu, ApresentacaoPartida, ApresentacaoRanking {
    private static final String SEPARADOR = "================================================================";

    private final PrintStream out;

    public ConsoleApresentacao(PrintStream out) {
        this.out = out;
    }

    // ---- Menu -----------------------------------------------------------------

    @Override
    public void exibirBoasVindas() {
        out.println(SEPARADOR);
        out.println("             MISSÃO MARTE UNIFOR - VERSÃO 10.0                  ");
        out.println(SEPARADOR);
        out.println("  Pilote sua nave, salve os passageiros e desvie dos perigos!   ");
        out.println(SEPARADOR);
    }

    @Override
    public void exibirMenu() {
        out.println("\n--- MENU PRINCIPAL ---");
        out.println("1. Iniciar Nova Missão");
        out.println("2. Visualizar Ranking Top 5");
        out.println("3. Resetar Histórico de Ranking");
        out.println("4. Sair do Jogo");
        out.println("----------------------");
    }

    @Override
    public void exibirOpcaoInvalida() {
        out.println("Opção inválida. Tente novamente.");
    }

    @Override
    public void exibirDespedida() {
        out.println("\nObrigado por jogar a Missão Marte Unifor!");
    }

    @Override
    public void exibirEntradaEncerrada() {
        out.println("\nEntrada encerrada. Obrigado por jogar!");
    }

    // ---- Partida --------------------------------------------------------------

    @Override
    public void exibirTamanhoDeMapaInvalido() {
        out.println("Entrada inválida, usando tamanho padrão (5).");
    }

    @Override
    public void exibirMapaPequenoDemais(Dificuldade dificuldade, int tamanhoMinimo) {
        out.printf("Mapa pequeno demais para a dificuldade %s. Use o tamanho mínimo %d.%n", dificuldade, tamanhoMinimo);
    }

    @Override
    public void exibirInicioDaMissao(Dificuldade dificuldade) {
        out.println("\nIniciando missão na dificuldade " + dificuldade + "...");
        out.println("Pressione Enter para decolar!");
    }

    @Override
    public void exibirMissaoNaoIniciada() {
        out.println("Entrada encerrada. Missão não iniciada.");
    }

    @Override
    public void exibirEstado(Partida partida) {
        Nave nave = partida.getMissao().getNave();
        out.print(MapaTextual.renderizar(partida.getMissao(), partida.getLimites(),
                partida.getPontuacao(), partida.getPiloto()));
        out.printf("Nave em (%d,%d) | Pontos: %d | Vidas: %d | A bordo: %d/%d | Restantes: %d%n",
                nave.getX(), nave.getY(), partida.getPontuacao(), nave.getVidas(),
                nave.getPassageiros().size(), nave.getCapacidade(),
                partida.getMissao().passageirosRestantes());
    }

    @Override
    public void exibirComandoInvalido() {
        out.println("Comando inválido.");
    }

    @Override
    public void exibirMissaoAbortadaPorFimDeEntrada() {
        out.println("Entrada encerrada. Missão abortada.");
    }

    @Override
    public void exibirEvento(Evento evento) {
        switch (evento.tipo()) {
            case PASSAGEIRO_EMBARCADO:
                out.printf("Passageiro %s embarcado com sucesso! +%d pontos!%n", evento.nome(), evento.valor());
                break;
            case SEM_PASSAGEIRO_NA_POSICAO:
                out.println("Nenhum passageiro nesta posição.");
                break;
            case NAVE_CHEIA:
                out.println("Nave cheia! Não há espaço para mais passageiros.");
                break;
            case MISSAO_ABORTADA:
                out.println("Missão abortada pelo piloto.");
                break;
            case COLISAO:
                out.printf("Alerta! Colisão detectada! Vidas restantes: %d%n", evento.valor());
                break;
            case NAVE_DESTRUIDA:
                out.println("GAME OVER! A nave foi destruída.");
                break;
            case PONTUACAO_ZERADA:
                out.println("Combustível/Pontuação zerada! Missão perdida.");
                break;
            case RETORNE_A_PLATAFORMA:
                out.println("✨ ALERTA: Todos os passageiros resgatados! Retorne para a Plataforma de Pouso 'L' em (0,0) para completar a missão.");
                break;
            case MISSAO_CUMPRIDA:
                out.println("\n" + SEPARADOR);
                out.println("🚀 DECOLAGEM AUTORIZADA! Nave acoplada à plataforma em (0,0).");
                out.println("Retornando à órbita marciana com todos os passageiros. Missão cumprida!");
                out.println(SEPARADOR);
                break;
            default:
                break;
        }
    }

    @Override
    public void exibirEstatisticas(ResumoPartida resumo) {
        out.println("Estatísticas da Partida:");
        out.printf(" - Pontuação Final: %d pontos%n", resumo.pontuacao());
        out.printf(" - Movimentos Efetuados: %d%n", resumo.movimentos());
        out.printf(" - Tempo de Jogo: %d segundos%n", resumo.tempoSegundos());
        out.printf(" - Passageiros Resgatados: %d%n", resumo.passageirosResgatados());

        if (resumo.bateuRecorde()) {
            out.println("🏆 Novo recorde absoluto do sistema!");
        } else if (resumo.existeRecordeASerBatido()) {
            out.printf(" - Recorde atual a ser batido: %d pontos (Piloto: %s)%n",
                    resumo.recordeAtual(), resumo.lider().map(EntradaRanking::nome).orElse(""));
        }
        out.println(SEPARADOR);
    }

    // ---- Ranking --------------------------------------------------------------

    @Override
    public void exibirRanking(Ranking ranking) {
        out.println("\n====== RANKING TOP 5 PILOTOS ======");
        if (ranking.estaVazio()) {
            out.println(" - Nenhum registro encontrado. Seja o primeiro a jogar!");
        } else {
            int pos = 1;
            for (EntradaRanking e : ranking.getEntradas()) {
                out.printf("%d. %s - %d pts | Dificuldade: %s | Coletados: %d | Tempo: %ds | %s%n",
                        pos++, e.nome(), e.pontuacao(), e.dificuldade(), e.passageirosColetados(),
                        e.tempoJogo(), e.dataHora());
            }
        }
        out.println("===================================");
    }

    @Override
    public void exibirRankingResetado() {
        out.println("Ranking resetado com sucesso!");
    }

    @Override
    public void exibirErroAoResetar(String motivo) {
        out.println("Erro ao resetar ranking: " + motivo);
    }

    @Override
    public void exibirResetCancelado() {
        out.println("Operação cancelada.");
    }

    @Override
    public void exibirErroAoSalvar(String motivo) {
        out.println("Não foi possível salvar o ranking: " + motivo);
    }

    @Override
    public void exibirNovoTop5() {
        out.println("Parabéns! Você entrou para o Top 5 de pilotos!");
    }
}
