package br.unifor.missaomarte.dominio;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Regras de uma partida: pontuação, turnos, colisões, derrota e vitória.
 *
 * <p>Estas regras viviam dentro do laço {@code while} de {@code Main.jogarPartida},
 * misturadas com {@code Scanner} e {@code System.out}. Agora esta classe não sabe que
 * o console existe: recebe {@link Comando}s e devolve {@link Evento}s. Por isso pode
 * ser testada sem terminal e reaproveitada na versão libGDX.</p>
 *
 * <p>A ordem das etapas de {@link #processar(Comando)} reproduz a do jogo original e
 * faz parte do comportamento preservado: ação do jogador, movimento dos inimigos,
 * colisão, pontuação zerada e, por fim, vitória.</p>
 */
public class Partida {
    private final String piloto;
    private final Dificuldade dificuldade;
    private final Missao missao;
    private final Limites limites;
    private final Random random;
    private final Clock relogio;
    private final long inicioMillis;

    private int pontuacao;
    private int movimentos;
    private long tempoDeJogoSegundos;
    private EstadoPartida estado = EstadoPartida.EM_ANDAMENTO;

    public Partida(String piloto, Dificuldade dificuldade, Missao missao, Limites limites,
                   Random random, Clock relogio) {
        this.piloto = piloto;
        this.dificuldade = dificuldade;
        this.missao = missao;
        this.limites = limites;
        this.random = random;
        this.relogio = relogio;
        this.pontuacao = dificuldade.getPontuacaoInicial();
        this.inicioMillis = relogio.millis();
    }

    public String getPiloto() {
        return piloto;
    }

    public Dificuldade getDificuldade() {
        return dificuldade;
    }

    public Missao getMissao() {
        return missao;
    }

    public Limites getLimites() {
        return limites;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public int getMovimentos() {
        return movimentos;
    }

    /** Duração em segundos; só é definida quando a partida termina em {@link EstadoPartida#VITORIA}. */
    public long getTempoDeJogoSegundos() {
        return tempoDeJogoSegundos;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public boolean emAndamento() {
        return estado == EstadoPartida.EM_ANDAMENTO;
    }

    /** Encerra a partida sem evento (usado quando a entrada acaba no meio da missão). */
    public void abortar() {
        estado = EstadoPartida.ABORTADA;
    }

    public List<Evento> processar(Comando comando) {
        if (!emAndamento()) {
            throw new IllegalStateException("A partida já terminou: " + estado);
        }
        List<Evento> eventos = new ArrayList<>();

        if (comando == Comando.ABORTAR) {
            estado = EstadoPartida.ABORTADA;
            eventos.add(Evento.semDetalhe(TipoEvento.MISSAO_ABORTADA));
            return eventos;
        }

        if (comando == Comando.EMBARCAR) {
            tentarEmbarcar(eventos);
        } else {
            comando.direcao().ifPresent(direcao -> {
                missao.getNave().mover(direcao, limites);
                pontuacao--;
                movimentos++;
            });
        }

        missao.moverInimigos(random, limites);

        verificarColisao(eventos);
        verificarPontuacaoZerada(eventos);
        verificarVitoria(eventos);
        return eventos;
    }

    private void tentarEmbarcar(List<Evento> eventos) {
        Passageiro passageiro = missao.passageiroNaPosicaoDaNave();
        if (passageiro == null) {
            eventos.add(Evento.semDetalhe(TipoEvento.SEM_PASSAGEIRO_NA_POSICAO));
        } else if (missao.embarcarPassageiroNaPosicao()) {
            int bonus = passageiro.getPontuacao();
            pontuacao += bonus;
            eventos.add(Evento.passageiroEmbarcado(passageiro.getNome(), bonus));
        } else {
            eventos.add(Evento.semDetalhe(TipoEvento.NAVE_CHEIA));
        }
    }

    private void verificarColisao(List<Evento> eventos) {
        if (!missao.verificaColisao()) {
            return;
        }
        Nave nave = missao.getNave();
        nave.perderVida();
        if (nave.getVidas() > 0) {
            eventos.add(Evento.colisao(nave.getVidas()));
        } else {
            eventos.add(Evento.semDetalhe(TipoEvento.NAVE_DESTRUIDA));
            estado = EstadoPartida.DERROTA;
        }
    }

    private void verificarPontuacaoZerada(List<Evento> eventos) {
        if (pontuacao <= 0) {
            eventos.add(Evento.semDetalhe(TipoEvento.PONTUACAO_ZERADA));
            estado = EstadoPartida.DERROTA;
        }
    }

    private void verificarVitoria(List<Evento> eventos) {
        if (!missao.todosEmbarcados() || !emAndamento()) {
            return;
        }
        if (missao.naveNaPlataformaDePouso()) {
            tempoDeJogoSegundos = (relogio.millis() - inicioMillis) / 1000;
            estado = EstadoPartida.VITORIA;
            eventos.add(Evento.semDetalhe(TipoEvento.MISSAO_CUMPRIDA));
        } else {
            eventos.add(Evento.semDetalhe(TipoEvento.RETORNE_A_PLATAFORMA));
        }
    }
}
