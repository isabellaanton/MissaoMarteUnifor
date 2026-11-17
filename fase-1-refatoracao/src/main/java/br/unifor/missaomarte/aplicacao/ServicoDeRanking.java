package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.EntradaRanking;
import br.unifor.missaomarte.dominio.Ranking;

/**
 * Casos de uso do ranking: consultar, zerar e registrar uma vitória. Coordena a
 * pergunta ao jogador, a regra do domínio ({@link Ranking}) e a persistência, mas não
 * imprime nem acessa arquivo diretamente.
 */
public class ServicoDeRanking {
    private final EntradaDoJogador entrada;
    private final ApresentacaoRanking apresentacao;
    private final RepositorioRanking repositorio;

    public ServicoDeRanking(EntradaDoJogador entrada, ApresentacaoRanking apresentacao,
                            RepositorioRanking repositorio) {
        this.entrada = entrada;
        this.apresentacao = apresentacao;
        this.repositorio = repositorio;
    }

    public Ranking carregar() {
        return repositorio.carregar();
    }

    public void exibir(Ranking ranking) {
        apresentacao.exibirRanking(ranking);
    }

    /** Pede confirmação e, se confirmada, apaga o histórico. Devolve o ranking resultante. */
    public Ranking resetar() {
        String confirmacao = entrada.perguntar("Você realmente deseja limpar o histórico de ranking? (s/n): ")
                .map(String::trim)
                .orElse("n")
                .toLowerCase();
        if (confirmacao.equals("s") || confirmacao.equals("sim")) {
            try {
                repositorio.limpar();
                apresentacao.exibirRankingResetado();
            } catch (FalhaDePersistencia e) {
                apresentacao.exibirErroAoResetar(e.getMessage());
            }
            return Ranking.vazio();
        }
        apresentacao.exibirResetCancelado();
        return repositorio.carregar();
    }

    /** Se a pontuação entra no Top 5, grava o novo ranking e avisa o jogador. */
    public void registrarSeQualificar(Ranking atual, EntradaRanking nova) {
        if (nova.pontuacao() > 0 && atual.qualificaParaTop(nova.pontuacao())) {
            try {
                repositorio.salvar(atual.comNovaEntrada(nova));
            } catch (FalhaDePersistencia e) {
                apresentacao.exibirErroAoSalvar(e.getMessage());
            }
            apresentacao.exibirNovoTop5();
        }
    }
}
