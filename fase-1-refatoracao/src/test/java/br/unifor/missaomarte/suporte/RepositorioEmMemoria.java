package br.unifor.missaomarte.suporte;

import br.unifor.missaomarte.aplicacao.FalhaDePersistencia;
import br.unifor.missaomarte.aplicacao.RepositorioRanking;
import br.unifor.missaomarte.dominio.Ranking;

/** Repositório falso em memória, com falhas programáveis. */
public class RepositorioEmMemoria implements RepositorioRanking {
    public Ranking guardado;
    public int vezesQueSalvou;
    public int vezesQueLimpou;
    public String falharAoSalvarCom;
    public String falharAoLimparCom;

    public RepositorioEmMemoria(Ranking inicial) {
        this.guardado = inicial;
    }

    @Override
    public Ranking carregar() {
        return guardado;
    }

    @Override
    public void salvar(Ranking ranking) {
        if (falharAoSalvarCom != null) {
            throw new FalhaDePersistencia(falharAoSalvarCom, null);
        }
        vezesQueSalvou++;
        guardado = ranking;
    }

    @Override
    public void limpar() {
        if (falharAoLimparCom != null) {
            throw new FalhaDePersistencia(falharAoLimparCom, null);
        }
        vezesQueLimpou++;
        guardado = Ranking.vazio();
    }
}
