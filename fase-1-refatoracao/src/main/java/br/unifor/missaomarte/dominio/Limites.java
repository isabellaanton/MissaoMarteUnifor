package br.unifor.missaomarte.dominio;

/**
 * Value Object com os limites (inclusivos) do mapa. Antes eram quatro inteiros
 * soltos ({@code minX, maxX, minY, maxY}) repassados por quase todos os métodos.
 */
public record Limites(int minX, int maxX, int minY, int maxY) {

    public Limites {
        if (minX > maxX || minY > maxY) {
            throw new IllegalArgumentException("Limites invertidos: " + minX + ".." + maxX + ", " + minY + ".." + maxY);
        }
    }

    /** Mapa quadrado centrado na origem: de {@code -tamanho} a {@code +tamanho} nos dois eixos. */
    public static Limites quadrado(int tamanho) {
        return new Limites(-tamanho, tamanho, -tamanho, tamanho);
    }

    public int largura() {
        return maxX - minX + 1;
    }

    public int altura() {
        return maxY - minY + 1;
    }

    public int totalDeCelulas() {
        return largura() * altura();
    }

    public boolean contem(int x, int y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
}
