package br.unifor.missaomarte.entrada;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleEntradaTest {
    private final ByteArrayOutputStream bytesDeSaida = new ByteArrayOutputStream();

    private ConsoleEntrada entradaCom(String texto) throws Exception {
        PrintStream saida = new PrintStream(bytesDeSaida, true, "UTF-8");
        return new ConsoleEntrada(new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8)),
                saida, StandardCharsets.UTF_8);
    }

    private String saida() throws Exception {
        return bytesDeSaida.toString("UTF-8");
    }

    @Test
    @DisplayName("devolve cada linha digitada, sem o fim de linha")
    void leLinhas() throws Exception {
        try (ConsoleEntrada entrada = entradaCom("primeira\nsegunda\n")) {
            assertEquals(Optional.of("primeira"), entrada.perguntar(""));
            assertEquals(Optional.of("segunda"), entrada.perguntar(""));
        }
    }

    @Test
    @DisplayName("não faz trim: a limpeza do texto é decisão da aplicação")
    void naoAlteraOTexto() throws Exception {
        try (ConsoleEntrada entrada = entradaCom("  João  \n")) {
            assertEquals(Optional.of("  João  "), entrada.perguntar(""));
        }
    }

    @Test
    @DisplayName("escreve a pergunta antes de ler; pergunta vazia não escreve nada")
    void mostraAPergunta() throws Exception {
        try (ConsoleEntrada entrada = entradaCom("a\nb\n")) {
            entrada.perguntar("Nome: ");
            assertEquals("Nome: ", saida());
            entrada.perguntar("");
            assertEquals("Nome: ", saida());
        }
    }

    @Test
    @DisplayName("sem mais entrada devolve vazio, mas ainda mostra a pergunta (como o original)")
    void fimDaEntrada() throws Exception {
        try (ConsoleEntrada entrada = entradaCom("")) {
            assertFalse(entrada.temMaisEntrada());
            assertTrue(entrada.perguntar("Comando: ").isEmpty());
            assertEquals("Comando: ", saida());
        }
    }

    @Test
    @DisplayName("temMaisEntrada acompanha as linhas restantes")
    void temMaisEntrada() throws Exception {
        try (ConsoleEntrada entrada = entradaCom("unica\n")) {
            assertTrue(entrada.temMaisEntrada());
            entrada.perguntar("");
            assertFalse(entrada.temMaisEntrada());
        }
    }

    @Test
    @DisplayName("última linha sem quebra de linha também é lida")
    void ultimaLinhaSemQuebra() throws Exception {
        try (ConsoleEntrada entrada = entradaCom("final")) {
            assertEquals(Optional.of("final"), entrada.perguntar(""));
        }
    }
}
