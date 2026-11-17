package br.unifor.missaomarte.aplicacao;

import java.util.Optional;

/**
 * Porta de entrada: de onde vêm as respostas do jogador. A implementação atual lê do
 * terminal; uma tela libGDX ou um app Android implementariam a mesma interface.
 */
public interface EntradaDoJogador {

    /**
     * Mostra a pergunta (pode ser vazia) e devolve a resposta digitada, sem tratamento.
     * Devolve vazio quando não há mais entrada (fim do arquivo / janela fechada).
     */
    Optional<String> perguntar(String pergunta);

    /** Ainda existe alguma resposta a ser lida? */
    boolean temMaisEntrada();
}
