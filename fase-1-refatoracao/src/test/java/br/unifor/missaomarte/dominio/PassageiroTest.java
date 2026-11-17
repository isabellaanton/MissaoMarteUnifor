package br.unifor.missaomarte.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PassageiroTest {

    @Test
    @DisplayName("professor vale 10, engenheiro 15 e astronauta 20 pontos")
    void pontuacaoPorTipo() {
        Passageiro professor = new Professor("Dr. Silva", 1, 2);
        Passageiro engenheiro = new Engenheiro("Eng. Rosa", 3, 4);
        Passageiro astronauta = new Astronauta("Ast. Maria", 5, 6);

        assertEquals(10, professor.getPontuacao());
        assertEquals(15, engenheiro.getPontuacao());
        assertEquals(20, astronauta.getPontuacao());
    }

    @Test
    @DisplayName("cada subclasse carrega o seu tipo e o rótulo exibido")
    void tipoERotulo() {
        assertEquals(TipoPassageiro.PROFESSOR, new Professor("a", 0, 0).getTipo());
        assertEquals(TipoPassageiro.ENGENHEIRO, new Engenheiro("a", 0, 0).getTipo());
        assertEquals(TipoPassageiro.ASTRONAUTA, new Astronauta("a", 0, 0).getTipo());
        assertEquals("Professor", TipoPassageiro.PROFESSOR.getRotulo());
        assertEquals("Engenheiro", TipoPassageiro.ENGENHEIRO.getRotulo());
        assertEquals("Astronauta", TipoPassageiro.ASTRONAUTA.getRotulo());
    }

    @Test
    @DisplayName("guarda nome e posição, e sabe dizer se está em uma célula")
    void nomeEPosicao() {
        Passageiro p = new Professor("Dr. Lima", -2, 3);
        assertEquals("Dr. Lima", p.getNome());
        assertEquals(-2, p.getX());
        assertEquals(3, p.getY());
        assertTrue(p.estaEm(-2, 3));
        assertFalse(p.estaEm(3, -2));
    }
}
