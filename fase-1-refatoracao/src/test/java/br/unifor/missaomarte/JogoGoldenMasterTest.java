package br.unifor.missaomarte;

import br.unifor.missaomarte.entrada.ConsoleEntrada;
import br.unifor.missaomarte.suporte.Textos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste de caracterização ("golden master") do jogo inteiro.
 *
 * <p>Cada pasta em {@code src/test/resources/golden/<cenario>} contém uma sessão completa:
 * {@code entrada.txt} (o que o jogador digitou), {@code seed.txt} (semente do Random),
 * opcionalmente {@code ranking-inicial.json}, e o que a <b>versão original de console</b>
 * produziu: {@code saida-esperada.txt} e, se existir, {@code ranking-final.json}. O teste
 * roda a versão refatorada com a mesma entrada, semente e relógio fixo e exige saída e
 * arquivo de ranking idênticos. É a evidência de que a refatoração preservou o
 * comportamento observável.</p>
 *
 * <p>Como as saídas foram geradas: o Main original foi copiado e apenas três pontos
 * foram fixados (semente do Random, relógio de partida e data/hora); ver
 * {@code docs/A3-A4-refatoracao.md}.</p>
 */
class JogoGoldenMasterTest {
    private static final Clock RELOGIO_FIXO = Clock.fixed(Instant.parse("2026-01-01T12:00:07Z"), ZoneOffset.UTC);

    private static String lerRecurso(String caminho) throws Exception {
        try (InputStream in = JogoGoldenMasterTest.class.getResourceAsStream(caminho)) {
            return in == null ? null : new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static List<String> cenarios() throws Exception {
        String indice = lerRecurso("/golden/indice.txt");
        assertNotNull(indice, "golden/indice.txt não encontrado nos recursos de teste");
        List<String> nomes = new ArrayList<>();
        for (String linha : indice.split("\\R")) {
            if (!linha.isBlank()) nomes.add(linha.trim());
        }
        return nomes;
    }

    private static String executar(String nome, Path pasta) throws Exception {
        Path ranking = pasta.resolve("ranking.json");
        String inicial = lerRecurso("/golden/" + nome + "/ranking-inicial.json");
        if (inicial != null) {
            Files.write(ranking, inicial.getBytes(StandardCharsets.UTF_8));
        }
        long semente = Long.parseLong(lerRecurso("/golden/" + nome + "/seed.txt").trim());
        byte[] entradaBytes = lerRecurso("/golden/" + nome + "/entrada.txt").getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintStream saida = new PrintStream(bytes, true, "UTF-8");
        try (ConsoleEntrada entrada = new ConsoleEntrada(
                new ByteArrayInputStream(entradaBytes), saida, StandardCharsets.UTF_8)) {
            Main.criarSessao(entrada, saida, ranking, new Random(semente), RELOGIO_FIXO).executar();
        }
        return Textos.normalizar(bytes.toString("UTF-8"));
    }

    @Test
    @DisplayName("sessões completas produzem a mesma saída e o mesmo ranking.json da versão original")
    void igualAoJogoOriginal(@TempDir Path pastaTemporaria) throws Exception {
        List<String> cenarios = cenarios();
        assertTrue(cenarios.size() >= 30, "esperava ao menos 30 cenários, achei " + cenarios.size());

        List<String> divergencias = new ArrayList<>();
        int indice = 0;
        for (String nome : cenarios) {
            Path pasta = Files.createDirectory(pastaTemporaria.resolve("c" + (indice++)));
            String obtida = executar(nome, pasta);
            String esperada = Textos.normalizar(lerRecurso("/golden/" + nome + "/saida-esperada.txt"));
            if (!esperada.equals(obtida)) {
                divergencias.add(nome + ": saída diferente");
            }

            String rankingEsperado = lerRecurso("/golden/" + nome + "/ranking-final.json");
            Path rankingObtido = pasta.resolve("ranking.json");
            if (rankingEsperado == null) {
                if (Files.exists(rankingObtido)) divergencias.add(nome + ": não deveria existir ranking.json");
            } else if (!Files.exists(rankingObtido)) {
                divergencias.add(nome + ": ranking.json deveria existir");
            } else if (!rankingEsperado.equals(new String(Files.readAllBytes(rankingObtido), StandardCharsets.UTF_8))) {
                divergencias.add(nome + ": ranking.json diferente");
            }
        }
        assertTrue(divergencias.isEmpty(), "cenários divergentes do original: " + divergencias);
    }
}
