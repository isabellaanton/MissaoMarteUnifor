package br.unifor.missaomarte.dominio;

/**
 * Elemento perigoso do mapa. {@link Asteroide} e {@link Inimigo} tinham o mesmo
 * método {@code colideCom} duplicado; agora a regra de colisão existe em um só lugar.
 */
public interface Obstaculo extends Posicionavel {

    default boolean colideCom(Nave nave) {
        return estaNaMesmaPosicaoQue(nave);
    }
}
