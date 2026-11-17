package br.unifor.missaomarte.infraestrutura;

import br.unifor.missaomarte.aplicacao.FalhaDePersistencia;
import br.unifor.missaomarte.dominio.Dificuldade;
import br.unifor.missaomarte.dominio.EntradaRanking;
import br.unifor.missaomarte.dominio.Ranking;
import br.unifor.missaomarte.suporte.Cenarios;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankingJsonRepositoryTest {

    private static void escrever(Path arquivo, String conteudo) throws Exception {
        Files.write(arquivo, conteudo.getBytes(StandardCharsets.UTF_8));
    }

    private static String ler(Path arquivo) throws Exception {
        return new String(Files.readAllBytes(arquivo), StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("arquivo inexistente significa ranking vazio")
    void arquivoInexistente(@TempDir Path pasta) {
        assertTrue(new RankingJsonRepository(pasta.resolve("ranking.json")).carregar().estaVazio());
    }

    @Test
    @DisplayName("grava exatamente no formato do arquivo da versão original")
    void formatoDoArquivo(@TempDir Path pasta) throws Exception {
        Path arquivo = pasta.resolve("ranking.json");
        RankingJsonRepository repo = new RankingJsonRepository(arquivo);
        repo.salvar(new Ranking(List.of(
                new EntradaRanking("Ana", 74, Dificuldade.FACIL, 4, "2026-01-01 12:00:07", 3),
                new EntradaRanking("Bia", 60, Dificuldade.DIFICIL, 5, "2025-12-01 10:00:00", 12))));

        assertEquals("[{\"name\":\"Ana\",\"score\":74,\"dificuldade\":\"FACIL\",\"passageirosColetados\":4,"
                + "\"dataHora\":\"2026-01-01 12:00:07\",\"tempoJogo\":3},"
                + "{\"name\":\"Bia\",\"score\":60,\"dificuldade\":\"DIFICIL\",\"passageirosColetados\":5,"
                + "\"dataHora\":\"2025-12-01 10:00:00\",\"tempoJogo\":12}]", ler(arquivo));
    }

    @Test
    @DisplayName("o que foi salvo é lido de volta igual")
    void salvarECarregar(@TempDir Path pasta) {
        RankingJsonRepository repo = new RankingJsonRepository(pasta.resolve("ranking.json"));
        Ranking original = new Ranking(List.of(
                new EntradaRanking("Ana", 74, Dificuldade.FACIL, 4, "2026-01-01 12:00:07", 3),
                new EntradaRanking("Bia", 60, Dificuldade.DIFICIL, 5, "2025-12-01 10:00:00", 12)));

        repo.salvar(original);

        assertEquals(original.getEntradas(), repo.carregar().getEntradas());
    }

    @Test
    @DisplayName("lê arquivos antigos e devolve as entradas ordenadas por pontuação")
    void leArquivoAntigoForaDeOrdem(@TempDir Path pasta) throws Exception {
        Path arquivo = pasta.resolve("ranking.json");
        escrever(arquivo, "[{\"name\":\"Baixo\",\"score\":10,\"dificuldade\":\"MEDIO\",\"passageirosColetados\":5,"
                + "\"dataHora\":\"2025-01-01 10:00:00\",\"tempoJogo\":9},"
                + "{\"name\":\"Alto\",\"score\":90,\"dificuldade\":\"FACIL\",\"passageirosColetados\":4,"
                + "\"dataHora\":\"2025-01-02 10:00:00\",\"tempoJogo\":7}]");

        List<EntradaRanking> entradas = new RankingJsonRepository(arquivo).carregar().getEntradas();

        assertEquals("Alto", entradas.get(0).nome());
        assertEquals(Dificuldade.FACIL, entradas.get(0).dificuldade());
        assertEquals("Baixo", entradas.get(1).nome());
        assertEquals(9L, entradas.get(1).tempoJogo());
    }

    @Test
    @DisplayName("aspas no nome do piloto sobrevivem à gravação e à leitura")
    void aspasNoNome(@TempDir Path pasta) throws Exception {
        Path arquivo = pasta.resolve("ranking.json");
        RankingJsonRepository repo = new RankingJsonRepository(arquivo);
        repo.salvar(new Ranking(List.of(Cenarios.entrada("Zé \"o\" Piloto", 50))));

        assertTrue(ler(arquivo).contains("\"name\":\"Zé \\\"o\\\" Piloto\""));
        assertEquals("Zé \"o\" Piloto", repo.carregar().getEntradas().get(0).nome());
    }

    @Test
    @DisplayName("arquivo que não é JSON vira ranking vazio, sem exceção")
    void arquivoCorrompido(@TempDir Path pasta) throws Exception {
        Path arquivo = pasta.resolve("ranking.json");
        escrever(arquivo, "isto não é json");
        assertTrue(new RankingJsonRepository(arquivo).carregar().estaVazio());
    }

    @Test
    @DisplayName("entradas sem nome ou sem pontuação válida são descartadas")
    void entradasIncompletas(@TempDir Path pasta) throws Exception {
        Path arquivo = pasta.resolve("ranking.json");
        escrever(arquivo, "[{\"name\":\"SemScore\"},{\"score\":5},{\"name\":\"Ruim\",\"score\":\"abc\"},"
                + "{\"name\":\"Bom\",\"score\":7}]");

        List<EntradaRanking> entradas = new RankingJsonRepository(arquivo).carregar().getEntradas();

        assertEquals(1, entradas.size());
        assertEquals("Bom", entradas.get(0).nome());
        assertEquals(Dificuldade.MEDIO, entradas.get(0).dificuldade());   // padrão quando o campo falta
    }

    @Test
    @DisplayName("[] e arquivo vazio significam ranking vazio")
    void arquivoVazioOuListaVazia(@TempDir Path pasta) throws Exception {
        Path arquivo = pasta.resolve("ranking.json");
        escrever(arquivo, "[]");
        assertTrue(new RankingJsonRepository(arquivo).carregar().estaVazio());
        escrever(arquivo, "");
        assertTrue(new RankingJsonRepository(arquivo).carregar().estaVazio());
    }

    @Test
    @DisplayName("carregar não corta o ranking em 5: quem corta é a regra do domínio ao gravar")
    void carregarNaoCorta(@TempDir Path pasta) throws Exception {
        Path arquivo = pasta.resolve("ranking.json");
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < 7; i++) {
            json.append(i > 0 ? "," : "").append("{\"name\":\"P").append(i).append("\",\"score\":").append(100 - i).append("}");
        }
        escrever(arquivo, json.append("]").toString());
        assertEquals(7, new RankingJsonRepository(arquivo).carregar().getEntradas().size());
    }

    @Test
    @DisplayName("limpar apaga o arquivo e não reclama se ele não existe")
    void limpar(@TempDir Path pasta) {
        Path arquivo = pasta.resolve("ranking.json");
        RankingJsonRepository repo = new RankingJsonRepository(arquivo);
        repo.salvar(new Ranking(List.of(Cenarios.entrada("Ana", 10))));
        assertTrue(Files.exists(arquivo));

        repo.limpar();
        assertFalse(Files.exists(arquivo));
        repo.limpar();   // segunda vez: nada a apagar, sem erro
    }

    @Test
    @DisplayName("falha de gravação vira FalhaDePersistencia com a mensagem do erro de E/S")
    void falhaAoSalvar(@TempDir Path pasta) {
        RankingJsonRepository repo = new RankingJsonRepository(pasta.resolve("nao-existe").resolve("ranking.json"));
        FalhaDePersistencia falha = assertThrows(FalhaDePersistencia.class,
                () -> repo.salvar(new Ranking(List.of(Cenarios.entrada("Ana", 10)))));
        assertTrue(falha.getCause() instanceof java.io.IOException);
        assertEquals(falha.getCause().getMessage(), falha.getMessage());
    }

    /**
     * LIMITAÇÃO CONHECIDA, HERDADA DO ORIGINAL (documentada, não corrigida nesta fase): o
     * leitor manual divide o texto por vírgula, então um nome como "Silva, Jr" é gravado,
     * mas a entrada é perdida na leitura seguinte. Se um dia o parser for trocado por uma
     * biblioteca JSON, este teste deve ser invertido.
     */
    @Test
    @DisplayName("limitação conhecida: nome com vírgula é perdido ao recarregar")
    void limitacaoNomeComVirgula(@TempDir Path pasta) {
        RankingJsonRepository repo = new RankingJsonRepository(pasta.resolve("ranking.json"));
        repo.salvar(new Ranking(List.of(Cenarios.entrada("Silva, Jr", 50))));
        assertTrue(repo.carregar().estaVazio());
    }
}
