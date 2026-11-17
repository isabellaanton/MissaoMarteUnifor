package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Ranking;

/** Porta de saída do ranking. */
public interface ApresentacaoRanking {
    void exibirRanking(Ranking ranking);

    void exibirRankingResetado();

    void exibirErroAoResetar(String motivo);

    void exibirResetCancelado();

    void exibirErroAoSalvar(String motivo);

    void exibirNovoTop5();
}
