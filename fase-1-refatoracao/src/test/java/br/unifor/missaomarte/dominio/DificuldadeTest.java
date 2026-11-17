package br.unifor.missaomarte.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Os números abaixo são os da versão de console original (dois switch no Main).
 * Se alguém mudar o balanceamento, estes testes avisam que é uma mudança intencional.
 */
class DificuldadeTest {

    @Test
    @DisplayName("pontuação inicial: fácil 30, médio 20, difícil 45")
    void pontuacaoInicial() {
        assertEquals(30, Dificuldade.FACIL.getPontuacaoInicial());
        assertEquals(20, Dificuldade.MEDIO.getPontuacaoInicial());
        assertEquals(45, Dificuldade.DIFICIL.getPontuacaoInicial());
    }

    @Test
    @DisplayName("fácil: 4 passageiros, 1 asteroide, 1 inimigo")
    void configuracaoFacil() {
        assertEquals(4, Dificuldade.FACIL.getQuantidadePassageiros());
        assertEquals(1, Dificuldade.FACIL.getQuantidadeAsteroides());
        assertEquals(1, Dificuldade.FACIL.getQuantidadeInimigos());
        assertEquals(6, Dificuldade.FACIL.totalDeElementos());
    }

    @Test
    @DisplayName("médio: 5 passageiros, 2 asteroides, 2 inimigos")
    void configuracaoMedio() {
        assertEquals(5, Dificuldade.MEDIO.getQuantidadePassageiros());
        assertEquals(2, Dificuldade.MEDIO.getQuantidadeAsteroides());
        assertEquals(2, Dificuldade.MEDIO.getQuantidadeInimigos());
        assertEquals(9, Dificuldade.MEDIO.totalDeElementos());
    }

    @Test
    @DisplayName("difícil: 5 passageiros, 3 asteroides, 3 inimigos")
    void configuracaoDificil() {
        assertEquals(5, Dificuldade.DIFICIL.getQuantidadePassageiros());
        assertEquals(3, Dificuldade.DIFICIL.getQuantidadeAsteroides());
        assertEquals(3, Dificuldade.DIFICIL.getQuantidadeInimigos());
        assertEquals(11, Dificuldade.DIFICIL.totalDeElementos());
    }

    @Test
    @DisplayName("deString reconhece facil/fácil e dificil/difícil, ignorando caixa e espaços")
    void deStringReconheceOsNiveis() {
        assertEquals(Dificuldade.FACIL, Dificuldade.deString("facil"));
        assertEquals(Dificuldade.FACIL, Dificuldade.deString("FÁCIL"));
        assertEquals(Dificuldade.FACIL, Dificuldade.deString("  Fácil  "));
        assertEquals(Dificuldade.DIFICIL, Dificuldade.deString("dificil"));
        assertEquals(Dificuldade.DIFICIL, Dificuldade.deString("Difícil"));
        assertEquals(Dificuldade.MEDIO, Dificuldade.deString("medio"));
    }

    @Test
    @DisplayName("texto desconhecido, vazio ou nulo cai em médio")
    void deStringUsaMedioComoPadrao() {
        assertEquals(Dificuldade.MEDIO, Dificuldade.deString("xyz"));
        assertEquals(Dificuldade.MEDIO, Dificuldade.deString(""));
        assertEquals(Dificuldade.MEDIO, Dificuldade.deString(null));
        assertEquals(Dificuldade.MEDIO, Dificuldade.deString("médio"));
    }

    @Test
    @DisplayName("toString devolve o rótulo com acento usado na tela")
    void rotulos() {
        assertEquals("Fácil", Dificuldade.FACIL.toString());
        assertEquals("Médio", Dificuldade.MEDIO.toString());
        assertEquals("Difícil", Dificuldade.DIFICIL.toString());
        assertEquals("Fácil", Dificuldade.FACIL.getRotulo());
    }

    @Test
    @DisplayName("name() continua sendo o valor gravado no ranking.json")
    void nomesDoEnumSaoOFormatoDoArquivo() {
        assertEquals("FACIL", Dificuldade.FACIL.name());
        assertEquals("MEDIO", Dificuldade.MEDIO.name());
        assertEquals("DIFICIL", Dificuldade.DIFICIL.name());
    }
}
