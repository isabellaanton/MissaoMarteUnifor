package br.unifor.missaomarte.dominio;

/**
 * Tipo do passageiro. Antes era uma {@code String} livre ("Professor", "Engenheiro"...)
 * e a tela descobria o símbolo com {@code instanceof}; agora é um tipo fechado que a
 * apresentação pode mapear sem conhecer as subclasses.
 */
public enum TipoPassageiro {
    PROFESSOR("Professor"),
    ENGENHEIRO("Engenheiro"),
    ASTRONAUTA("Astronauta");

    private final String rotulo;

    TipoPassageiro(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }
}
