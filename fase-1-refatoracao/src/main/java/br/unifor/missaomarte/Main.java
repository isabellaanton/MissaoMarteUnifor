package br.unifor.missaomarte;

import br.unifor.missaomarte.aplicacao.EntradaDoJogador;
import br.unifor.missaomarte.aplicacao.ExecutorDePartida;
import br.unifor.missaomarte.aplicacao.ServicoDeRanking;
import br.unifor.missaomarte.aplicacao.SessaoDeJogo;
import br.unifor.missaomarte.apresentacao.ConsoleApresentacao;
import br.unifor.missaomarte.entrada.ConsoleEntrada;
import br.unifor.missaomarte.entrada.InterpretadorPorTecla;
import br.unifor.missaomarte.infraestrutura.RankingJsonRepository;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.Random;

/**
 * Raiz de composição: o único lugar que conhece todas as classes concretas e liga as
 * peças. Não contém regra de jogo, leitura de teclado nem formatação de texto.
 */
public final class Main {
    private static final Path ARQUIVO_DE_RANKING = Paths.get("ranking.json");

    private Main() {
    }

    public static void main(String[] args) {
        try (ConsoleEntrada entrada = new ConsoleEntrada(System.in, System.out, Charset.defaultCharset())) {
            criarSessao(entrada, System.out, ARQUIVO_DE_RANKING, new Random(), Clock.systemDefaultZone())
                    .executar();
        }
    }

    /** Monta o jogo com todas as dependências injetadas (também usado pelos testes). */
    public static SessaoDeJogo criarSessao(EntradaDoJogador entrada, PrintStream saida, Path arquivoDeRanking,
                                           Random random, Clock relogio) {
        ConsoleApresentacao apresentacao = new ConsoleApresentacao(saida);
        ServicoDeRanking servicoDeRanking = new ServicoDeRanking(
                entrada, apresentacao, new RankingJsonRepository(arquivoDeRanking));
        ExecutorDePartida executor = new ExecutorDePartida(
                entrada, new InterpretadorPorTecla(), apresentacao, servicoDeRanking, random, relogio);
        return new SessaoDeJogo(entrada, apresentacao, executor, servicoDeRanking);
    }
}
