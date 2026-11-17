package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Ranking;

/** Laço do menu principal. Só decide para qual caso de uso encaminhar a opção escolhida. */
public class SessaoDeJogo {
    private final EntradaDoJogador entrada;
    private final ApresentacaoMenu apresentacao;
    private final ExecutorDePartida executorDePartida;
    private final ServicoDeRanking servicoDeRanking;

    public SessaoDeJogo(EntradaDoJogador entrada, ApresentacaoMenu apresentacao,
                        ExecutorDePartida executorDePartida, ServicoDeRanking servicoDeRanking) {
        this.entrada = entrada;
        this.apresentacao = apresentacao;
        this.executorDePartida = executorDePartida;
        this.servicoDeRanking = servicoDeRanking;
    }

    public void executar() {
        Ranking ranking = servicoDeRanking.carregar();

        apresentacao.exibirBoasVindas();

        boolean rodando = true;
        while (rodando) {
            if (!entrada.temMaisEntrada()) {
                apresentacao.exibirEntradaEncerrada();
                break;
            }
            apresentacao.exibirMenu();
            String opcao = entrada.perguntar("Escolha uma opção: ").map(String::trim).orElse("1");
            switch (opcao) {
                case "1":
                    executorDePartida.executar(ranking);
                    ranking = servicoDeRanking.carregar();
                    break;
                case "2":
                    servicoDeRanking.exibir(ranking);
                    break;
                case "3":
                    ranking = servicoDeRanking.resetar();
                    break;
                case "4":
                    rodando = false;
                    apresentacao.exibirDespedida();
                    break;
                default:
                    apresentacao.exibirOpcaoInvalida();
            }
        }
    }
}
