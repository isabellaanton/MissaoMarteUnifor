package br.unifor.missaomarte.dominio;

import java.util.Optional;

/**
 * Intenção do jogador, independente do dispositivo (teclado, toque, gamepad). O
 * domínio só conhece comandos; quem traduz "w" ou um toque na tela é a camada de entrada.
 */
public enum Comando {
    MOVER_CIMA(Direcao.CIMA),
    MOVER_BAIXO(Direcao.BAIXO),
    MOVER_ESQUERDA(Direcao.ESQUERDA),
    MOVER_DIREITA(Direcao.DIREITA),
    EMBARCAR(null),
    ABORTAR(null);

    private final Direcao direcao;

    Comando(Direcao direcao) {
        this.direcao = direcao;
    }

    /** Direção do movimento, se este comando for um movimento. */
    public Optional<Direcao> direcao() {
        return Optional.ofNullable(direcao);
    }
}
