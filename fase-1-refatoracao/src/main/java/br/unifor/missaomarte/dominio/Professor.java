package br.unifor.missaomarte.dominio;

public class Professor extends Passageiro {
    public Professor(String nome, int x, int y) {
        super(nome, TipoPassageiro.PROFESSOR, x, y);
    }

    @Override
    public int getPontuacao() {
        return 10;
    }
}
