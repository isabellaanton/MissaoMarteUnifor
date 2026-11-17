package br.unifor.missaomarte.aplicacao;

/** Porta de saída do menu principal. Interface pequena de propósito (ISP). */
public interface ApresentacaoMenu {
    void exibirBoasVindas();

    void exibirMenu();

    void exibirOpcaoInvalida();

    void exibirDespedida();

    void exibirEntradaEncerrada();
}
