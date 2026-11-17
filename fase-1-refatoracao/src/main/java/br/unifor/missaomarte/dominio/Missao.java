package br.unifor.missaomarte.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Estado do tabuleiro: a nave e tudo que está no mapa. É o "Information Expert" das
 * perguntas espaciais (há colisão? quem está nesta célula? a célula está ocupada?).
 */
public class Missao {
    /** Coordenada da plataforma de pouso (símbolo 'L' no mapa). */
    public static final int POUSO_X = 0;
    public static final int POUSO_Y = 0;

    private final Nave nave;
    private final List<Passageiro> passageiros = new ArrayList<>();
    private final List<Asteroide> asteroides = new ArrayList<>();
    private final List<Inimigo> inimigos = new ArrayList<>();

    public Missao(Nave nave) {
        this.nave = nave;
    }

    public Nave getNave() {
        return nave;
    }

    public List<Passageiro> getPassageiros() {
        return Collections.unmodifiableList(passageiros);
    }

    public List<Asteroide> getAsteroides() {
        return Collections.unmodifiableList(asteroides);
    }

    public List<Inimigo> getInimigos() {
        return Collections.unmodifiableList(inimigos);
    }

    public void addPassageiro(Passageiro passageiro) {
        passageiros.add(passageiro);
    }

    public void addAsteroide(Asteroide asteroide) {
        asteroides.add(asteroide);
    }

    public void addInimigo(Inimigo inimigo) {
        inimigos.add(inimigo);
    }

    public int passageirosRestantes() {
        return passageiros.size();
    }

    public boolean verificaColisao() {
        for (Asteroide a : asteroides) {
            if (a.colideCom(nave)) return true;
        }
        for (Inimigo i : inimigos) {
            if (i.colideCom(nave)) return true;
        }
        return false;
    }

    public void moverInimigos(Random random, Limites limites) {
        for (Inimigo i : inimigos) {
            i.mover(random, limites);
        }
    }

    /** Passageiro que está na mesma célula da nave, ou {@code null}. */
    public Passageiro passageiroNaPosicaoDaNave() {
        for (Passageiro p : passageiros) {
            if (p.estaNaMesmaPosicaoQue(nave)) return p;
        }
        return null;
    }

    public boolean embarcarPassageiroNaPosicao() {
        Iterator<Passageiro> it = passageiros.iterator();
        while (it.hasNext()) {
            Passageiro p = it.next();
            if (p.estaNaMesmaPosicaoQue(nave)) {
                boolean ok = nave.embarcar(p);
                if (ok) it.remove();
                return ok;
            }
        }
        return false;
    }

    public boolean todosEmbarcados() {
        return passageiros.isEmpty();
    }

    public boolean naveNaPlataformaDePouso() {
        return nave.estaEm(POUSO_X, POUSO_Y);
    }

    /** Célula já tomada pela nave, por um passageiro, asteroide ou inimigo? */
    public boolean posicaoOcupada(int x, int y) {
        if (nave.estaEm(x, y)) return true;
        for (Passageiro p : passageiros) {
            if (p.estaEm(x, y)) return true;
        }
        for (Asteroide a : asteroides) {
            if (a.estaEm(x, y)) return true;
        }
        for (Inimigo i : inimigos) {
            if (i.estaEm(x, y)) return true;
        }
        return false;
    }
}
