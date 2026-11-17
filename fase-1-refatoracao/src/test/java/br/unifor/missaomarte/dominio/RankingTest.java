package br.unifor.missaomarte.dominio;

import br.unifor.missaomarte.suporte.Cenarios;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankingTest {

    private static Ranking comPontuacoes(int... pontos) {
        Ranking r = Ranking.vazio();
        for (int i = 0; i < pontos.length; i++) {
            r = r.comNovaEntrada(Cenarios.entrada("J" + i, pontos[i]));
        }
        return r;
    }

    private static List<Integer> pontuacoes(Ranking r) {
        return r.getEntradas().stream().map(EntradaRanking::pontuacao).toList();
    }

    @Test
    @DisplayName("ranking vazio não tem líder e qualquer pontuação entra")
    void vazio() {
        Ranking r = Ranking.vazio();
        assertTrue(r.estaVazio());
        assertFalse(r.lider().isPresent());
        assertTrue(r.qualificaParaTop(1));
    }

    @Test
    @DisplayName("mantém ordem decrescente de pontuação")
    void ordenaPorPontuacao() {
        assertEquals(List.of(90, 50, 30), pontuacoes(comPontuacoes(30, 90, 50)));
        assertEquals(90, comPontuacoes(30, 90, 50).lider().orElseThrow().pontuacao());
    }

    @Test
    @DisplayName("guarda no máximo 5 entradas e descarta a pior")
    void limitaAoTop5() {
        Ranking r = comPontuacoes(10, 20, 30, 40, 50, 60);
        assertEquals(List.of(60, 50, 40, 30, 20), pontuacoes(r));
    }

    @Test
    @DisplayName("com menos de 5 entradas qualquer pontuação qualifica")
    void aindaHaVaga() {
        assertTrue(comPontuacoes(100, 90, 80, 70).qualificaParaTop(1));
    }

    @Test
    @DisplayName("com 5 entradas só qualifica quem supera o último (empate não entra)")
    void rankingCheio() {
        Ranking r = comPontuacoes(100, 90, 80, 70, 60);
        assertFalse(r.qualificaParaTop(60));
        assertFalse(r.qualificaParaTop(10));
        assertTrue(r.qualificaParaTop(61));
    }

    @Test
    @DisplayName("em caso de empate, quem chegou primeiro fica na frente (ordenação estável)")
    void empateMantemQuemChegouPrimeiro() {
        Ranking r = Ranking.vazio()
                .comNovaEntrada(Cenarios.entrada("Antigo", 80))
                .comNovaEntrada(Cenarios.entrada("Novo", 80));
        assertEquals("Antigo", r.getEntradas().get(0).nome());
        assertEquals("Novo", r.getEntradas().get(1).nome());
    }

    @Test
    @DisplayName("comNovaEntrada não altera o ranking original (imutável)")
    void imutavel() {
        Ranking original = comPontuacoes(50);
        Ranking novo = original.comNovaEntrada(Cenarios.entrada("X", 70));
        assertEquals(1, original.getEntradas().size());
        assertEquals(2, novo.getEntradas().size());
        assertThrows(UnsupportedOperationException.class, () -> original.getEntradas().add(Cenarios.entrada("Y", 1)));
    }

    @Test
    @DisplayName("ordenado() ordena entradas carregadas fora de ordem, sem cortar em 5")
    void ordenadoNaoCorta() {
        Ranking r = Ranking.ordenado(List.of(
                Cenarios.entrada("a", 10), Cenarios.entrada("b", 70), Cenarios.entrada("c", 40),
                Cenarios.entrada("d", 20), Cenarios.entrada("e", 90), Cenarios.entrada("f", 30),
                Cenarios.entrada("g", 5)));
        assertEquals(List.of(90, 70, 40, 30, 20, 10, 5), pontuacoes(r));
    }
}
