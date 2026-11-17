package br.unifor.missaomarte.dominio;

import java.util.Random;

/** Obstáculo móvel: a cada turno dá no máximo um passo em uma direção sorteada. */
public class Inimigo implements Obstaculo {
    private int x;
    private int y;

    public Inimigo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    /**
     * Sorteia uma direção (0..3) e anda um passo se não ultrapassar os limites.
     * A ordem das chamadas ao {@link Random} é parte do comportamento observável
     * (partidas com a mesma semente precisam ser reproduzíveis).
     */
    public void mover(Random random, Limites limites) {
        int direcao = random.nextInt(4);
        switch (direcao) {
            case 0:
                if (x < limites.maxX()) x++;
                break;
            case 1:
                if (x > limites.minX()) x--;
                break;
            case 2:
                if (y < limites.maxY()) y++;
                break;
            case 3:
                if (y > limites.minY()) y--;
                break;
            default:
                break;
        }
    }
}
