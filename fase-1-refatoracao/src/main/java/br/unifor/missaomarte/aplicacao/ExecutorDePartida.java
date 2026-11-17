package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Comando;
import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.EntradaRanking;
import br.unifor.missaomarte.dominio.EstadoPartida;
import br.unifor.missaomarte.dominio.Evento;
import br.unifor.missaomarte.dominio.GeradorDeMissao;
import br.unifor.missaomarte.dominio.Limites;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Partida;
import br.unifor.missaomarte.dominio.Ranking;
import br.unifor.missaomarte.dominio.ResumoPartida;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Caso de uso "jogar uma partida" (GRASP Controller): conduz a conversa com o jogador,
 * monta a {@link Partida}, repassa os comandos e entrega os eventos à apresentação.
 * Nenhuma regra de pontuação, colisão ou vitória mora aqui, e nada é impresso aqui.
 */
public class ExecutorDePartida {
    static final String PILOTO_PADRAO = "Piloto Anônimo";
    static final int TAMANHO_PADRAO_DO_MAPA = 5;
    private static final DateTimeFormatter FORMATO_DATA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EntradaDoJogador entrada;
    private final InterpretadorDeComandos interpretador;
    private final ApresentacaoPartida apresentacao;
    private final ServicoDeRanking servicoDeRanking;
    private final Random random;
    private final Clock relogio;

    public ExecutorDePartida(EntradaDoJogador entrada, InterpretadorDeComandos interpretador,
                             ApresentacaoPartida apresentacao, ServicoDeRanking servicoDeRanking,
                             Random random, Clock relogio) {
        this.entrada = entrada;
        this.interpretador = interpretador;
        this.apresentacao = apresentacao;
        this.servicoDeRanking = servicoDeRanking;
        this.random = random;
        this.relogio = relogio;
    }

    public void executar(Ranking ranking) {
        String piloto = perguntar("\nDigite o nome do piloto: ", PILOTO_PADRAO);
        if (piloto.isEmpty()) {
            piloto = PILOTO_PADRAO;
        }
        Dificuldade dificuldade = Dificuldade.deString(
                perguntar("Escolha a Dificuldade (facil/medio/dificil): ", "medio"));
        int tamanho = lerTamanhoDoMapa();

        int minimo = GeradorDeMissao.tamanhoMinimo(dificuldade);
        if (tamanho < minimo) {
            apresentacao.exibirMapaPequenoDemais(dificuldade, minimo);
            return;
        }

        apresentacao.exibirInicioDaMissao(dificuldade);
        if (!entrada.temMaisEntrada()) {
            apresentacao.exibirMissaoNaoIniciada();
            return;
        }
        entrada.perguntar("");

        Limites limites = Limites.quadrado(tamanho);
        Missao missao = new GeradorDeMissao(random).gerar(limites, dificuldade);
        Partida partida = new Partida(piloto, dificuldade, missao, limites, random, relogio);

        conduzir(partida);

        if (partida.getEstado() == EstadoPartida.VITORIA) {
            registrarVitoria(partida, ranking);
        }
    }

    private void conduzir(Partida partida) {
        while (partida.emAndamento()) {
            apresentacao.exibirEstado(partida);

            String texto = perguntar("Comando (w/s/a/d/c/q): ", "").toLowerCase();
            if (texto.isEmpty()) {
                if (!entrada.temMaisEntrada()) {
                    apresentacao.exibirMissaoAbortadaPorFimDeEntrada();
                    partida.abortar();
                }
                continue;
            }

            Optional<Comando> comando = interpretador.interpretar(texto);
            if (comando.isEmpty()) {
                apresentacao.exibirComandoInvalido();
                continue;
            }

            List<Evento> eventos = partida.processar(comando.get());
            eventos.forEach(apresentacao::exibirEvento);
        }
    }

    private void registrarVitoria(Partida partida, Ranking ranking) {
        int passageiros = partida.getMissao().getNave().getPassageiros().size();
        apresentacao.exibirEstatisticas(new ResumoPartida(
                partida.getPontuacao(), partida.getMovimentos(), partida.getTempoDeJogoSegundos(),
                passageiros, ranking.lider()));

        EntradaRanking nova = new EntradaRanking(
                partida.getPiloto(),
                partida.getPontuacao(),
                partida.getDificuldade(),
                passageiros,
                LocalDateTime.now(relogio).format(FORMATO_DATA_HORA),
                partida.getTempoDeJogoSegundos());
        servicoDeRanking.registrarSeQualificar(ranking, nova);
    }

    private int lerTamanhoDoMapa() {
        try {
            int tamanho = Integer.parseInt(
                    perguntar("Tamanho do mapa (ex: 5 para mapa de -5 a +5): ", String.valueOf(TAMANHO_PADRAO_DO_MAPA)));
            return tamanho > 0 ? tamanho : TAMANHO_PADRAO_DO_MAPA;
        } catch (NumberFormatException e) {
            apresentacao.exibirTamanhoDeMapaInvalido();
            return TAMANHO_PADRAO_DO_MAPA;
        }
    }

    private String perguntar(String pergunta, String valorSeSemEntrada) {
        return entrada.perguntar(pergunta).map(String::trim).orElse(valorSeSemEntrada);
    }
}
