package br.unifor.missaomarte.dominio;

public class Engenheiro extends Passageiro {
    public Engenheiro(String nome, int x, int y) {
        super(nome, TipoPassageiro.ENGENHEIRO, x, y);
    }

    @Override
    public int getPontuacao() {
        return 15;
    }
}
