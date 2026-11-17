package br.unifor.missaomarte.suporte;

import br.unifor.missaomarte.dominio.Asteroide;
import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.EntradaRanking;
import br.unifor.missaomarte.dominio.Inimigo;
import br.unifor.missaomarte.dominio.Limites;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Nave;
import br.unifor.missaomarte.dominio.Partida;
import br.unifor.missaomarte.dominio.Passageiro;

import java.time.Clock;
import java.util.Random;

/** Montagem de cenários pequenos e determinísticos, sem sorteio. */
public final class Cenarios {
    public static final Limites MAPA_3X3 = Limites.quadrado(3);

    private Cenarios() {
    }

    public static Missao missao(int capacidadeDaNave, Passageiro[] passageiros, Asteroide[] asteroides, Inimigo[] inimigos) {
        Missao missao = new Missao(new Nave("T-1", capacidadeDaNave));
        for (Passageiro p : passageiros) missao.addPassageiro(p);
        for (Asteroide a : asteroides) missao.addAsteroide(a);
        for (Inimigo i : inimigos) missao.addInimigo(i);
        return missao;
    }

    public static Partida partida(Missao missao, Dificuldade dificuldade, Random random, Clock relogio) {
        return new Partida("Tester", dificuldade, missao, MAPA_3X3, random, relogio);
    }

    public static EntradaRanking entrada(String nome, int pontuacao) {
        return new EntradaRanking(nome, pontuacao, Dificuldade.FACIL, 4, "2025-12-01 10:00:00", 12);
    }

    /** Random cujo {@code nextInt(int)} devolve sempre o mesmo valor (controla o passo dos inimigos). */
    public static Random sorteioFixo(int valor) {
        return new Random() {
            private static final long serialVersionUID = 1L;

            @Override
            public int nextInt(int bound) {
                return valor;
            }
        };
    }
}
