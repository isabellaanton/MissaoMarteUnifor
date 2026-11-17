package br.unifor.missaomarte.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LimitesTest {

    @Test
    @DisplayName("quadrado(5) vai de -5 a +5 nos dois eixos")
    void quadrado() {
        Limites l = Limites.quadrado(5);
        assertEquals(-5, l.minX());
        assertEquals(5, l.maxX());
        assertEquals(-5, l.minY());
        assertEquals(5, l.maxY());
        assertEquals(11, l.largura());
        assertEquals(11, l.altura());
        assertEquals(121, l.totalDeCelulas());
    }

    @Test
    @DisplayName("contem considera as bordas inclusivas")
    void contem() {
        Limites l = Limites.quadrado(1);
        assertTrue(l.contem(0, 0));
        assertTrue(l.contem(-1, 1));
        assertFalse(l.contem(2, 0));
        assertFalse(l.contem(0, -2));
    }

    @Test
    @DisplayName("limites invertidos são rejeitados")
    void invertidos() {
        assertThrows(IllegalArgumentException.class, () -> new Limites(2, 1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Limites(0, 0, 3, -3));
    }
}
