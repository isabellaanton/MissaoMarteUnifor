package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Ranking;

/** Porta de persistência do ranking (hoje um arquivo JSON; amanhã pode ser a API do servidor). */
public interface RepositorioRanking {

    /** Ranking salvo, ordenado por pontuação; vazio se não existir ou não puder ser lido. */
    Ranking carregar();

    /** @throws FalhaDePersistencia se não conseguir gravar */
    void salvar(Ranking ranking);

    /** @throws FalhaDePersistencia se não conseguir apagar */
    void limpar();
}
