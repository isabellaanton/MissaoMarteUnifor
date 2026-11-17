package br.unifor.missaomarte.dominio;

import java.util.Optional;

/**
 * Números finais de uma partida vencida, junto com o líder do ranking <i>antes</i> de
 * ela ser registrada (para dizer se bateu o recorde).
 */
public record ResumoPartida(int pontuacao, int movimentos, long tempoSegundos,
                            int passageirosResgatados, Optional<EntradaRanking> lider) {

    /** Pontuação do recorde vigente; 0 quando o ranking está vazio. */
    public int recordeAtual() {
        return lider.map(EntradaRanking::pontuacao).orElse(0);
    }

    public boolean bateuRecorde() {
        return recordeAtual() > 0 && pontuacao > recordeAtual();
    }

    public boolean existeRecordeASerBatido() {
        return recordeAtual() > 0;
    }
}
