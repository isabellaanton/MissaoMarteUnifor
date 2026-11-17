package br.unifor.missaomarte.entrada;

import br.unifor.missaomarte.aplicacao.EntradaDoJogador;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.Scanner;

/**
 * Adaptador de entrada: lê linhas do terminal. É o único lugar do jogo que conhece
 * {@link Scanner}. A pergunta é escrita aqui porque, no console, perguntar e esperar a
 * resposta são a mesma interação.
 */
public class ConsoleEntrada implements EntradaDoJogador, AutoCloseable {
    private final Scanner scanner;
    private final PrintStream saida;

    public ConsoleEntrada(InputStream entrada, PrintStream saida, Charset charset) {
        this.scanner = new Scanner(entrada, charset);
        this.saida = saida;
    }

    @Override
    public Optional<String> perguntar(String pergunta) {
        if (pergunta != null && !pergunta.isEmpty()) {
            saida.print(pergunta);
        }
        if (scanner.hasNextLine()) {
            return Optional.of(scanner.nextLine());
        }
        return Optional.empty();
    }

    @Override
    public boolean temMaisEntrada() {
        return scanner.hasNextLine();
    }

    @Override
    public void close() {
        scanner.close();
    }
}
