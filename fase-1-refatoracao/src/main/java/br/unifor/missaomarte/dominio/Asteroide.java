package br.unifor.missaomarte.dominio;

/** Obstáculo fixo: só causa dano se a nave estiver na mesma célula. */
public class Asteroide implements Obstaculo {
    private final int x;
    private final int y;

    public Asteroide(int x, int y) {
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
}
