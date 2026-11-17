package br.unifor.missaomarte.dominio;

/**
 * Os parâmetros de cada nível agora vivem no próprio enum. Antes estavam espalhados
 * em dois {@code switch}/{@code if-else} no {@code Main}: criar um nível novo exigia
 * editar o fluxo do jogo (violação do Open/Closed).
 */
public enum Dificuldade {
    //            rótulo    pontos  passageiros  asteroides  inimigos
    FACIL("Fácil", 30, 4, 1, 1),
    MEDIO("Médio", 20, 5, 2, 2),
    DIFICIL("Difícil", 45, 5, 3, 3);

    private final String rotulo;
    private final int pontuacaoInicial;
    private final int quantidadePassageiros;
    private final int quantidadeAsteroides;
    private final int quantidadeInimigos;

    Dificuldade(String rotulo, int pontuacaoInicial, int quantidadePassageiros,
                int quantidadeAsteroides, int quantidadeInimigos) {
        this.rotulo = rotulo;
        this.pontuacaoInicial = pontuacaoInicial;
        this.quantidadePassageiros = quantidadePassageiros;
        this.quantidadeAsteroides = quantidadeAsteroides;
        this.quantidadeInimigos = quantidadeInimigos;
    }

    public String getRotulo() {
        return rotulo;
    }

    public int getPontuacaoInicial() {
        return pontuacaoInicial;
    }

    public int getQuantidadePassageiros() {
        return quantidadePassageiros;
    }

    public int getQuantidadeAsteroides() {
        return quantidadeAsteroides;
    }

    public int getQuantidadeInimigos() {
        return quantidadeInimigos;
    }

    /** Total de elementos (sem a nave) que precisam caber no mapa. */
    public int totalDeElementos() {
        return quantidadePassageiros + quantidadeAsteroides + quantidadeInimigos;
    }

    /** Converte o texto digitado; qualquer valor desconhecido (ou nulo) vira {@link #MEDIO}. */
    public static Dificuldade deString(String s) {
        if (s == null) return MEDIO;
        switch (s.trim().toLowerCase()) {
            case "facil":
            case "fácil":
                return FACIL;
            case "dificil":
            case "difícil":
                return DIFICIL;
            default:
                return MEDIO;
        }
    }

    @Override
    public String toString() {
        return rotulo;
    }
}
