package br.unifor.missaomarte.dominio;

import br.unifor.missaomarte.suporte.Cenarios;
import br.unifor.missaomarte.suporte.RelogioManual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regras da partida testadas sem console, sem Scanner e sem sorteio: o mapa é montado à
 * mão. É a prova de que as regras não dependem da interface.
 */
class PartidaTest {
    private static final Passageiro[] NENHUM_PASSAGEIRO = {};
    private static final Asteroide[] NENHUM_ASTEROIDE = {};
    private static final Inimigo[] NENHUM_INIMIGO = {};

    private RelogioManual relogio;

    @BeforeEach
    void iniciarRelogio() {
        relogio = new RelogioManual(1_000);
    }

    private Partida partida(Missao missao) {
        return Cenarios.partida(missao, Dificuldade.FACIL, new Random(1), relogio);
    }

    private static Passageiro[] passageiros(Passageiro... p) {
        return p;
    }

    private static List<TipoEvento> tipos(List<Evento> eventos) {
        return eventos.stream().map(Evento::tipo).toList();
    }

    @Test
    @DisplayName("a partida começa em andamento com a pontuação inicial da dificuldade")
    void estadoInicial() {
        Partida p = partida(Cenarios.missao(5, passageiros(new Professor("P", 3, 3)), NENHUM_ASTEROIDE, NENHUM_INIMIGO));
        assertTrue(p.emAndamento());
        assertEquals(EstadoPartida.EM_ANDAMENTO, p.getEstado());
        assertEquals(30, p.getPontuacao());
        assertEquals(0, p.getMovimentos());
        assertEquals("Tester", p.getPiloto());
    }

    @Test
    @DisplayName("cada movimento custa 1 ponto e conta como movimento")
    void movimentoCustaUmPonto() {
        Partida p = partida(Cenarios.missao(5, passageiros(new Professor("P", 3, 3)), NENHUM_ASTEROIDE, NENHUM_INIMIGO));

        List<Evento> eventos = p.processar(Comando.MOVER_DIREITA);

        assertEquals(29, p.getPontuacao());
        assertEquals(1, p.getMovimentos());
        assertEquals(1, p.getMissao().getNave().getX());
        assertTrue(eventos.isEmpty());
        assertTrue(p.emAndamento());
    }

    @Test
    @DisplayName("bater na borda do mapa também custa ponto")
    void movimentoContraAParedeCustaPonto() {
        Partida p = partida(Cenarios.missao(5, passageiros(new Professor("P", 3, 3)), NENHUM_ASTEROIDE, NENHUM_INIMIGO));
        for (int i = 0; i < 4; i++) {
            p.processar(Comando.MOVER_ESQUERDA);
        }
        assertEquals(-3, p.getMissao().getNave().getX());
        assertEquals(26, p.getPontuacao());
        assertEquals(4, p.getMovimentos());
    }

    @Test
    @DisplayName("embarcar soma os pontos do tipo do passageiro e não conta como movimento")
    void embarcarSomaBonus() {
        Partida p = partida(Cenarios.missao(5,
                passageiros(new Astronauta("Ast. Maria", 1, 0), new Professor("Longe", 3, 3)),
                NENHUM_ASTEROIDE, NENHUM_INIMIGO));
        p.processar(Comando.MOVER_DIREITA);

        List<Evento> eventos = p.processar(Comando.EMBARCAR);

        assertEquals(29 + 20, p.getPontuacao());
        assertEquals(1, p.getMovimentos());
        assertEquals(1, eventos.size());
        assertEquals(Evento.passageiroEmbarcado("Ast. Maria", 20), eventos.get(0));
    }

    @Test
    @DisplayName("embarcar sem passageiro na célula só avisa")
    void embarcarSemPassageiro() {
        Partida p = partida(Cenarios.missao(5, passageiros(new Professor("P", 3, 3)), NENHUM_ASTEROIDE, NENHUM_INIMIGO));

        List<Evento> eventos = p.processar(Comando.EMBARCAR);

        assertEquals(List.of(TipoEvento.SEM_PASSAGEIRO_NA_POSICAO), tipos(eventos));
        assertEquals(30, p.getPontuacao());
        assertEquals(0, p.getMovimentos());
    }

    @Test
    @DisplayName("nave cheia: o passageiro não embarca e não há pontos")
    void naveCheia() {
        Partida p = partida(Cenarios.missao(1,
                passageiros(new Professor("Um", 1, 0), new Engenheiro("Dois", 2, 0)),
                NENHUM_ASTEROIDE, NENHUM_INIMIGO));
        p.processar(Comando.MOVER_DIREITA);
        p.processar(Comando.EMBARCAR);
        p.processar(Comando.MOVER_DIREITA);
        int pontosAntes = p.getPontuacao();

        List<Evento> eventos = p.processar(Comando.EMBARCAR);

        assertEquals(List.of(TipoEvento.NAVE_CHEIA), tipos(eventos));
        assertEquals(pontosAntes, p.getPontuacao());
        assertEquals(1, p.getMissao().passageirosRestantes());
    }

    @Test
    @DisplayName("colidir com asteroide tira uma vida e informa as vidas restantes")
    void colisaoTiraVida() {
        Partida p = partida(Cenarios.missao(5, passageiros(new Professor("P", 3, 3)),
                new Asteroide[]{new Asteroide(1, 0)}, NENHUM_INIMIGO));

        List<Evento> eventos = p.processar(Comando.MOVER_DIREITA);

        assertEquals(2, p.getMissao().getNave().getVidas());
        assertEquals(List.of(Evento.colisao(2)), eventos);
        assertTrue(p.emAndamento());
    }

    @Test
    @DisplayName("a terceira colisão destrói a nave e encerra a partida em derrota")
    void tresColisoesDestroemANave() {
        Partida p = partida(Cenarios.missao(5, passageiros(new Professor("P", 3, 3)),
                new Asteroide[]{new Asteroide(1, 0)}, NENHUM_INIMIGO));
        p.processar(Comando.MOVER_DIREITA);   // 1ª colisão
        p.processar(Comando.EMBARCAR);        // continua sobre o asteroide: 2ª colisão

        List<Evento> eventos = p.processar(Comando.EMBARCAR);   // 3ª colisão

        assertEquals(List.of(TipoEvento.SEM_PASSAGEIRO_NA_POSICAO, TipoEvento.NAVE_DESTRUIDA), tipos(eventos));
        assertEquals(0, p.getMissao().getNave().getVidas());
        assertEquals(EstadoPartida.DERROTA, p.getEstado());
        assertFalse(p.emAndamento());
    }

    @Test
    @DisplayName("pontuação que chega a zero encerra a partida em derrota")
    void pontuacaoZeradaEncerra() {
        Partida p = partida(Cenarios.missao(5, passageiros(new Professor("P", 3, 3)), NENHUM_ASTEROIDE, NENHUM_INIMIGO));
        for (int i = 0; i < 29; i++) {
            p.processar(i % 2 == 0 ? Comando.MOVER_CIMA : Comando.MOVER_BAIXO);
        }
        assertEquals(1, p.getPontuacao());
        assertTrue(p.emAndamento());

        List<Evento> eventos = p.processar(Comando.MOVER_CIMA);

        assertEquals(0, p.getPontuacao());
        assertEquals(List.of(TipoEvento.PONTUACAO_ZERADA), tipos(eventos));
        assertEquals(EstadoPartida.DERROTA, p.getEstado());
    }

    @Test
    @DisplayName("os inimigos andam depois de cada ação do jogador")
    void inimigosAndamAposCadaAcao() {
        Inimigo inimigo = new Inimigo(2, 2);
        Partida p = Cenarios.partida(
                Cenarios.missao(5, passageiros(new Professor("P", -3, -3)), NENHUM_ASTEROIDE, new Inimigo[]{inimigo}),
                Dificuldade.FACIL, Cenarios.sorteioFixo(0), relogio);   // sorteio 0 = passo para a direita

        p.processar(Comando.MOVER_CIMA);
        assertEquals(3, inimigo.getX());
        p.processar(Comando.EMBARCAR);
        assertEquals(3, inimigo.getX());   // já está na borda: continua em 3
        assertEquals(2, inimigo.getY());
    }

    @Test
    @DisplayName("abortar encerra a missão sem mover os inimigos")
    void abortarNaoMoveInimigos() {
        Inimigo inimigo = new Inimigo(2, 2);
        Partida p = Cenarios.partida(
                Cenarios.missao(5, passageiros(new Professor("P", -3, -3)), NENHUM_ASTEROIDE, new Inimigo[]{inimigo}),
                Dificuldade.FACIL, Cenarios.sorteioFixo(1), relogio);

        List<Evento> eventos = p.processar(Comando.ABORTAR);

        assertEquals(List.of(TipoEvento.MISSAO_ABORTADA), tipos(eventos));
        assertEquals(EstadoPartida.ABORTADA, p.getEstado());
        assertEquals(2, inimigo.getX());
        assertEquals(30, p.getPontuacao());
    }

    @Test
    @DisplayName("abortar() encerra silenciosamente (fim da entrada no meio da missão)")
    void abortarSemEvento() {
        Partida p = partida(Cenarios.missao(5, passageiros(new Professor("P", 3, 3)), NENHUM_ASTEROIDE, NENHUM_INIMIGO));
        p.abortar();
        assertEquals(EstadoPartida.ABORTADA, p.getEstado());
    }

    @Test
    @DisplayName("com todos a bordo, a vitória só acontece ao voltar para a plataforma (0,0)")
    void vitoriaExigeVoltarAPlataforma() {
        Partida p = partida(Cenarios.missao(5, passageiros(new Professor("Prof", 1, 0)), NENHUM_ASTEROIDE, NENHUM_INIMIGO));
        p.processar(Comando.MOVER_DIREITA);

        List<Evento> aoEmbarcar = p.processar(Comando.EMBARCAR);

        assertEquals(List.of(TipoEvento.PASSAGEIRO_EMBARCADO, TipoEvento.RETORNE_A_PLATAFORMA), tipos(aoEmbarcar));
        assertTrue(p.emAndamento());

        relogio.avancar(7_500);
        List<Evento> aoVoltar = p.processar(Comando.MOVER_ESQUERDA);

        assertEquals(List.of(TipoEvento.MISSAO_CUMPRIDA), tipos(aoVoltar));
        assertEquals(EstadoPartida.VITORIA, p.getEstado());
        assertEquals(30 - 1 + 10 - 1, p.getPontuacao());
        assertEquals(2, p.getMovimentos());
        assertEquals(7, p.getTempoDeJogoSegundos());
    }

    @Test
    @DisplayName("voltar à plataforma com passageiros em terra não vence")
    void naoVenceSemTodosOsPassageiros() {
        Partida p = partida(Cenarios.missao(5, passageiros(new Professor("P", 3, 3)), NENHUM_ASTEROIDE, NENHUM_INIMIGO));
        p.processar(Comando.MOVER_DIREITA);
        List<Evento> eventos = p.processar(Comando.MOVER_ESQUERDA);

        assertTrue(p.getMissao().naveNaPlataformaDePouso());
        assertTrue(eventos.isEmpty());
        assertTrue(p.emAndamento());
    }

    @Test
    @DisplayName("o tempo de jogo é arredondado para baixo, em segundos")
    void tempoArredondadoParaBaixo() {
        Partida p = partida(Cenarios.missao(5, passageiros(new Professor("Prof", 1, 0)), NENHUM_ASTEROIDE, NENHUM_INIMIGO));
        p.processar(Comando.MOVER_DIREITA);
        p.processar(Comando.EMBARCAR);
        relogio.avancar(59_999);
        p.processar(Comando.MOVER_ESQUERDA);
        assertEquals(59, p.getTempoDeJogoSegundos());
    }

    @Test
    @DisplayName("processar depois do fim da partida é erro de programação")
    void processarAposOFim() {
        Partida p = partida(Cenarios.missao(5, passageiros(new Professor("P", 3, 3)), NENHUM_ASTEROIDE, NENHUM_INIMIGO));
        p.processar(Comando.ABORTAR);
        assertThrows(IllegalStateException.class, () -> p.processar(Comando.MOVER_CIMA));
    }

    @Test
    @DisplayName("mesma semente e mesmos comandos produzem a mesma partida")
    void reprodutivel() {
        String comandos = "wwddcssaacw";
        assertEquals(simular(5, comandos), simular(5, comandos));
    }

    private String simular(long semente, String comandos) {
        Random random = new Random(semente);
        Limites limites = Limites.quadrado(3);
        Missao missao = new GeradorDeMissao(random).gerar(limites, Dificuldade.DIFICIL);
        Partida p = new Partida("x", Dificuldade.DIFICIL, missao, limites, random, relogio);
        StringBuilder log = new StringBuilder();
        for (char c : comandos.toCharArray()) {
            if (!p.emAndamento()) break;
            Comando comando = switch (c) {
                case 'w' -> Comando.MOVER_CIMA;
                case 's' -> Comando.MOVER_BAIXO;
                case 'a' -> Comando.MOVER_ESQUERDA;
                case 'd' -> Comando.MOVER_DIREITA;
                default -> Comando.EMBARCAR;
            };
            log.append(p.processar(comando)).append('|').append(p.getPontuacao()).append(';');
        }
        return log.toString();
    }
}
