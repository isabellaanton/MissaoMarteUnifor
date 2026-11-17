package br.unifor.missaomarte.dominio;

/**
 * Um registro do ranking. Antes era a classe privada {@code Main.RankingEntry}, com
 * campos acessados diretamente de qualquer método do {@code Main}.
 *
 * @param dataHora texto no formato {@code yyyy-MM-dd HH:mm:ss}
 */
public record EntradaRanking(String nome, int pontuacao, Dificuldade dificuldade,
                             int passageirosColetados, String dataHora, long tempoJogo) {
}
