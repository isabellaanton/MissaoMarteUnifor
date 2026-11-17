package br.unifor.missaomarte.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeradorDeMissaoTest {

    private Missao gerar(long semente, int tamanho, Dificuldade dificuldade) {
        return new GeradorDeMissao(new Random(semente)).gerar(Limites.quadrado(tamanho), dificuldade);
    }

    private static List<String> descrever(Missao m) {
        List<String> posicoes = new ArrayList<>();
        for (Passageiro p : m.getPassageiros()) posicoes.add("P:" + p.getNome() + "@" + p.getX() + "," + p.getY());
        for (Asteroide a : m.getAsteroides()) posicoes.add("A@" + a.getX() + "," + a.getY());
        for (Inimigo i : m.getInimigos()) posicoes.add("I@" + i.getX() + "," + i.getY());
        return posicoes;
    }

    @Test
    @DisplayName("gera a quantidade de elementos definida por cada dificuldade")
    void quantidadesPorDificuldade() {
        for (Dificuldade d : Dificuldade.values()) {
            Missao m = gerar(7, 5, d);
            assertEquals(d.getQuantidadePassageiros(), m.getPassageiros().size());
            assertEquals(d.getQuantidadeAsteroides(), m.getAsteroides().size());
            assertEquals(d.getQuantidadeInimigos(), m.getInimigos().size());
        }
    }

    @Test
    @DisplayName("nave de capacidade 5 e id A-1 começando na plataforma de pouso")
    void naveInicial() {
        Missao m = gerar(1, 3, Dificuldade.FACIL);
        assertEquals("A-1", m.getNave().getId());
        assertEquals(5, m.getNave().getCapacidade());
        assertTrue(m.naveNaPlataformaDePouso());
    }

    @Test
    @DisplayName("nenhum elemento fica fora do mapa, na célula da nave ou sobre outro elemento")
    void posicoesValidasEUnicas() {
        Limites limites = Limites.quadrado(2);
        for (long semente = 1; semente <= 60; semente++) {
            for (Dificuldade d : Dificuldade.values()) {
                Missao m = new GeradorDeMissao(new Random(semente)).gerar(limites, d);
                Set<String> usadas = new HashSet<>();
                usadas.add("0,0");
                List<Posicionavel> todos = new ArrayList<>();
                todos.addAll(m.getPassageiros());
                todos.addAll(m.getAsteroides());
                todos.addAll(m.getInimigos());
                for (Posicionavel p : todos) {
                    assertTrue(limites.contem(p.getX(), p.getY()), "fora do mapa (semente " + semente + ")");
                    assertTrue(usadas.add(p.getX() + "," + p.getY()), "célula repetida (semente " + semente + ")");
                }
            }
        }
    }

    @Test
    @DisplayName("a mesma semente produz exatamente o mesmo mapa")
    void reprodutivelComAMesmaSemente() {
        assertEquals(descrever(gerar(42, 5, Dificuldade.DIFICIL)), descrever(gerar(42, 5, Dificuldade.DIFICIL)));
        assertFalse(descrever(gerar(42, 5, Dificuldade.DIFICIL)).equals(descrever(gerar(43, 5, Dificuldade.DIFICIL))));
    }

    /**
     * Teste de caracterização: estas posições foram obtidas executando o jogo ORIGINAL
     * (Main de console) com {@code new Random(1)}, nível fácil e mapa 3. Garantem que a
     * ordem das chamadas ao Random não mudou na refatoração.
     */
    @Test
    @DisplayName("semente 1, fácil, mapa 3 reproduz o mapa da versão original")
    void mapaIgualAoDaVersaoOriginal() {
        Missao m = gerar(1, 3, Dificuldade.FACIL);
        assertEquals(List.of(
                "P:Dr. Silva@1,1",
                "P:Eng. Rosa@-2,-3",
                "P:Dr. Lima@3,3",
                "P:Eng. Carlos@-3,-2",
                "A@0,3",
                "I@2,2"), descrever(m));
    }

    @Test
    @DisplayName("os passageiros seguem o ciclo Professor, Engenheiro, Professor, Engenheiro, Astronauta")
    void tiposDePassageiroEmCiclo() {
        Missao m = gerar(3, 5, Dificuldade.MEDIO);
        List<TipoPassageiro> tipos = new ArrayList<>();
        for (Passageiro p : m.getPassageiros()) tipos.add(p.getTipo());
        assertEquals(List.of(TipoPassageiro.PROFESSOR, TipoPassageiro.ENGENHEIRO, TipoPassageiro.PROFESSOR,
                TipoPassageiro.ENGENHEIRO, TipoPassageiro.ASTRONAUTA), tipos);
    }

    @Test
    @DisplayName("tamanho mínimo: fácil 1, médio 2, difícil 2")
    void tamanhoMinimo() {
        assertEquals(1, GeradorDeMissao.tamanhoMinimo(Dificuldade.FACIL));
        assertEquals(2, GeradorDeMissao.tamanhoMinimo(Dificuldade.MEDIO));
        assertEquals(2, GeradorDeMissao.tamanhoMinimo(Dificuldade.DIFICIL));
    }

    /**
     * MUDANÇA INTENCIONAL em relação ao original: lá, mapa 1 no nível médio (9 elementos
     * para 8 células livres) entrava em laço infinito. Agora é rejeitado com exceção.
     */
    @Test
    @DisplayName("mapa pequeno demais é rejeitado em vez de travar (corrige laço infinito do original)")
    void mapaPequenoDemais() {
        assertThrows(IllegalArgumentException.class, () -> gerar(1, 1, Dificuldade.MEDIO));
        assertThrows(IllegalArgumentException.class, () -> gerar(1, 1, Dificuldade.DIFICIL));
    }

    @Test
    @DisplayName("no tamanho mínimo todas as dificuldades terminam de gerar o mapa")
    void tamanhoMinimoFunciona() {
        for (Dificuldade d : Dificuldade.values()) {
            for (long semente = 1; semente <= 20; semente++) {
                Missao m = gerar(semente, GeradorDeMissao.tamanhoMinimo(d), d);
                assertEquals(d.getQuantidadePassageiros(), m.getPassageiros().size());
            }
        }
    }

    @Test
    @DisplayName("o mapa precisa conter a plataforma de pouso")
    void limitesSemOPouso() {
        GeradorDeMissao gerador = new GeradorDeMissao(new Random(1));
        assertThrows(IllegalArgumentException.class,
                () -> gerador.gerar(new Limites(1, 5, 1, 5), Dificuldade.FACIL));
    }
}
