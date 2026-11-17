package br.unifor.missaomarte.suporte;

import br.unifor.missaomarte.aplicacao.ApresentacaoMenu;
import br.unifor.missaomarte.aplicacao.ApresentacaoPartida;
import br.unifor.missaomarte.aplicacao.ApresentacaoRanking;
import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.Evento;
import br.unifor.missaomarte.dominio.Partida;
import br.unifor.missaomarte.dominio.Ranking;
import br.unifor.missaomarte.dominio.ResumoPartida;

import java.util.ArrayList;
import java.util.List;

/**
 * Apresentação falsa: em vez de imprimir, anota o que foi pedido. Permite testar os
 * casos de uso sem console, o que só é possível porque dependem de interfaces.
 */
public class RegistroDeApresentacao implements ApresentacaoMenu, ApresentacaoPartida, ApresentacaoRanking {
    public final List<String> chamadas = new ArrayList<>();
    public final List<Evento> eventos = new ArrayList<>();
    public ResumoPartida ultimoResumo;
    public Ranking ultimoRankingExibido;
    public Partida ultimaPartida;

    public long quantas(String chamada) {
        return chamadas.stream().filter(c -> c.equals(chamada)).count();
    }

    @Override
    public void exibirBoasVindas() {
        chamadas.add("boasVindas");
    }

    @Override
    public void exibirMenu() {
        chamadas.add("menu");
    }

    @Override
    public void exibirOpcaoInvalida() {
        chamadas.add("opcaoInvalida");
    }

    @Override
    public void exibirDespedida() {
        chamadas.add("despedida");
    }

    @Override
    public void exibirEntradaEncerrada() {
        chamadas.add("entradaEncerrada");
    }

    @Override
    public void exibirTamanhoDeMapaInvalido() {
        chamadas.add("tamanhoInvalido");
    }

    @Override
    public void exibirMapaPequenoDemais(Dificuldade dificuldade, int tamanhoMinimo) {
        chamadas.add("mapaPequeno:" + dificuldade.name() + ":" + tamanhoMinimo);
    }

    @Override
    public void exibirInicioDaMissao(Dificuldade dificuldade) {
        chamadas.add("inicioMissao:" + dificuldade.name());
    }

    @Override
    public void exibirMissaoNaoIniciada() {
        chamadas.add("missaoNaoIniciada");
    }

    @Override
    public void exibirEstado(Partida partida) {
        ultimaPartida = partida;
        chamadas.add("estado");
    }

    @Override
    public void exibirComandoInvalido() {
        chamadas.add("comandoInvalido");
    }

    @Override
    public void exibirMissaoAbortadaPorFimDeEntrada() {
        chamadas.add("abortadaPorFimDeEntrada");
    }

    @Override
    public void exibirEvento(Evento evento) {
        eventos.add(evento);
        chamadas.add("evento:" + evento.tipo().name());
    }

    @Override
    public void exibirEstatisticas(ResumoPartida resumo) {
        ultimoResumo = resumo;
        chamadas.add("estatisticas");
    }

    @Override
    public void exibirRanking(Ranking ranking) {
        ultimoRankingExibido = ranking;
        chamadas.add("ranking");
    }

    @Override
    public void exibirRankingResetado() {
        chamadas.add("rankingResetado");
    }

    @Override
    public void exibirErroAoResetar(String motivo) {
        chamadas.add("erroAoResetar:" + motivo);
    }

    @Override
    public void exibirResetCancelado() {
        chamadas.add("resetCancelado");
    }

    @Override
    public void exibirErroAoSalvar(String motivo) {
        chamadas.add("erroAoSalvar:" + motivo);
    }

    @Override
    public void exibirNovoTop5() {
        chamadas.add("novoTop5");
    }
}
