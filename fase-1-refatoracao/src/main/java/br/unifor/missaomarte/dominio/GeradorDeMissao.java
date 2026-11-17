package br.unifor.missaomarte.dominio;

import java.util.Random;

/**
 * Fábrica de missões (GRASP Creator / Pure Fabrication): tira do fluxo do jogo a
 * responsabilidade de sortear posições e montar o tabuleiro.
 *
 * <p>A sequência de chamadas ao {@link Random} é idêntica à da versão de console
 * original (passageiros, depois asteroides, depois inimigos; x antes de y; sorteio
 * repetido quando a célula já está ocupada). Isso garante que a mesma semente
 * produza exatamente o mesmo mapa.</p>
 */
public class GeradorDeMissao {
    public static final String ID_DA_NAVE = "A-1";
    public static final int CAPACIDADE_DA_NAVE = 5;

    private final Random random;

    public GeradorDeMissao(Random random) {
        this.random = random;
    }

    /**
     * Menor tamanho de mapa quadrado ({@code -t..+t}) em que todos os elementos da
     * dificuldade cabem sem sobrepor a nave. A versão original travava em laço
     * infinito quando o mapa era menor que isso (ex.: tamanho 1 no nível Médio).
     */
    public static int tamanhoMinimo(Dificuldade dificuldade) {
        int tamanho = 1;
        while (Limites.quadrado(tamanho).totalDeCelulas() - 1 < dificuldade.totalDeElementos()) {
            tamanho++;
        }
        return tamanho;
    }

    public Missao gerar(Limites limites, Dificuldade dificuldade) {
        if (!limites.contem(Missao.POUSO_X, Missao.POUSO_Y)) {
            throw new IllegalArgumentException("O mapa precisa conter a plataforma de pouso (0,0)");
        }
        int celulasLivres = limites.totalDeCelulas() - 1;
        if (celulasLivres < dificuldade.totalDeElementos()) {
            throw new IllegalArgumentException("Mapa pequeno demais para a dificuldade " + dificuldade
                    + ": " + celulasLivres + " células livres para " + dificuldade.totalDeElementos() + " elementos");
        }

        Nave nave = new Nave(ID_DA_NAVE, CAPACIDADE_DA_NAVE);
        Missao missao = new Missao(nave);

        while (missao.passageirosRestantes() < dificuldade.getQuantidadePassageiros()) {
            int x = sortearX(limites);
            int y = sortearY(limites);
            if (missao.posicaoOcupada(x, y)) continue;
            missao.addPassageiro(criarPassageiro(missao.passageirosRestantes(), x, y));
        }

        while (missao.getAsteroides().size() < dificuldade.getQuantidadeAsteroides()) {
            int x = sortearX(limites);
            int y = sortearY(limites);
            if (missao.posicaoOcupada(x, y)) continue;
            missao.addAsteroide(new Asteroide(x, y));
        }

        while (missao.getInimigos().size() < dificuldade.getQuantidadeInimigos()) {
            int x = sortearX(limites);
            int y = sortearY(limites);
            if (missao.posicaoOcupada(x, y)) continue;
            missao.addInimigo(new Inimigo(x, y));
        }

        return missao;
    }

    private int sortearX(Limites limites) {
        return random.nextInt(limites.maxX() - limites.minX() + 1) + limites.minX();
    }

    private int sortearY(Limites limites) {
        return random.nextInt(limites.maxY() - limites.minY() + 1) + limites.minY();
    }

    private static Passageiro criarPassageiro(int indice, int x, int y) {
        switch (indice % 5) {
            case 0: return new Professor("Dr. Silva", x, y);
            case 1: return new Engenheiro("Eng. Rosa", x, y);
            case 2: return new Professor("Dr. Lima", x, y);
            case 3: return new Engenheiro("Eng. Carlos", x, y);
            default: return new Astronauta("Ast. Maria", x, y);
        }
    }
}
