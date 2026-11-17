package br.unifor.missaomarte.dominio;

public class Astronauta extends Passageiro {
    public Astronauta(String nome, int x, int y) {
        super(nome, TipoPassageiro.ASTRONAUTA, x, y);
    }

    @Override
    public int getPontuacao() {
        return 20;
    }
}
