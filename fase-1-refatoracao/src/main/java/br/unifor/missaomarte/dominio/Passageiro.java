package br.unifor.missaomarte.dominio;

/**
 * Pessoa a ser resgatada. A pontuação é decidida pelas subclasses (polimorfismo);
 * a classe é abstrata porque um "passageiro genérico" não existe no jogo.
 */
public abstract class Passageiro implements Posicionavel {
    private final String nome;
    private final TipoPassageiro tipo;
    private final int x;
    private final int y;

    protected Passageiro(String nome, TipoPassageiro tipo, int x, int y) {
        this.nome = nome;
        this.tipo = tipo;
        this.x = x;
        this.y = y;
    }

    public String getNome() {
        return nome;
    }

    public TipoPassageiro getTipo() {
        return tipo;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    /** Pontos concedidos ao embarcar este passageiro. */
    public abstract int getPontuacao();
}
