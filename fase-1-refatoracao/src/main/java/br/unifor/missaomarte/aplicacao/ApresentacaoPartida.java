package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.Evento;
import br.unifor.missaomarte.dominio.Partida;
import br.unifor.missaomarte.dominio.ResumoPartida;

/** Porta de saída de uma partida: tudo que o jogador precisa ver enquanto joga. */
public interface ApresentacaoPartida {
    void exibirTamanhoDeMapaInvalido();

    void exibirMapaPequenoDemais(Dificuldade dificuldade, int tamanhoMinimo);

    void exibirInicioDaMissao(Dificuldade dificuldade);

    void exibirMissaoNaoIniciada();

    /** Mapa e linha de status do turno atual. */
    void exibirEstado(Partida partida);

    void exibirComandoInvalido();

    void exibirMissaoAbortadaPorFimDeEntrada();

    void exibirEvento(Evento evento);

    void exibirEstatisticas(ResumoPartida resumo);
}
