package br.unifor.missaomarte.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Regras do ranking Top 5. Imutável: {@link #comNovaEntrada(EntradaRanking)} devolve
 * um novo ranking. Não sabe de arquivo nem de JSON (isso é do repositório).
 */
public final class Ranking {
    public static final int CAPACIDADE = 5;

    private static final Comparator<EntradaRanking> POR_PONTUACAO_DECRESCENTE =
            Comparator.comparingInt(EntradaRanking::pontuacao).reversed();

    private final List<EntradaRanking> entradas;

    /** Usa as entradas na ordem recebida (o repositório já entrega ordenadas). */
    public Ranking(List<EntradaRanking> entradas) {
        this.entradas = Collections.unmodifiableList(new ArrayList<>(entradas));
    }

    public static Ranking vazio() {
        return new Ranking(List.of());
    }

    public List<EntradaRanking> getEntradas() {
        return entradas;
    }

    public boolean estaVazio() {
        return entradas.isEmpty();
    }

    /** Melhor colocado (primeira entrada), se houver. */
    public Optional<EntradaRanking> lider() {
        return entradas.isEmpty() ? Optional.empty() : Optional.of(entradas.get(0));
    }

    /** Há vaga no Top 5 ou a pontuação supera a do último colocado? */
    public boolean qualificaParaTop(int pontuacao) {
        if (entradas.size() < CAPACIDADE) {
            return true;
        }
        return pontuacao > entradas.get(entradas.size() - 1).pontuacao();
    }

    /**
     * Acrescenta a entrada, ordena por pontuação (empates mantêm quem chegou primeiro,
     * pois a ordenação é estável) e mantém só os {@value #CAPACIDADE} melhores.
     */
    public Ranking comNovaEntrada(EntradaRanking nova) {
        List<EntradaRanking> todas = new ArrayList<>(entradas);
        todas.add(nova);
        todas.sort(POR_PONTUACAO_DECRESCENTE);
        return new Ranking(todas.subList(0, Math.min(CAPACIDADE, todas.size())));
    }

    /** Ordena por pontuação decrescente (estável). Usado ao carregar do armazenamento. */
    public static Ranking ordenado(List<EntradaRanking> entradas) {
        List<EntradaRanking> copia = new ArrayList<>(entradas);
        copia.sort(POR_PONTUACAO_DECRESCENTE);
        return new Ranking(copia);
    }
}
