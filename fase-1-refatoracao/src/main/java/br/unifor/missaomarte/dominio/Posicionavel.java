package br.unifor.missaomarte.dominio;

/**
 * Qualquer elemento que ocupa uma célula do mapa. Elimina a comparação
 * {@code a.getX() == b.getX() && a.getY() == b.getY()} repetida em várias classes.
 */
public interface Posicionavel {

    int getX();

    int getY();

    default boolean estaEm(int x, int y) {
        return getX() == x && getY() == y;
    }

    default boolean estaNaMesmaPosicaoQue(Posicionavel outro) {
        return estaEm(outro.getX(), outro.getY());
    }
}
