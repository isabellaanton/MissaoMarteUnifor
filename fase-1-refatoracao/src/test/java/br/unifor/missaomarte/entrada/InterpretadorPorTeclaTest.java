package br.unifor.missaomarte.entrada;

import br.unifor.missaomarte.dominio.Comando;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InterpretadorPorTeclaTest {
    private final InterpretadorPorTecla interpretador = new InterpretadorPorTecla();

    @Test
    @DisplayName("w/s/a/d movem, c embarca, q aborta")
    void teclasValidas() {
        assertEquals(Optional.of(Comando.MOVER_CIMA), interpretador.interpretar("w"));
        assertEquals(Optional.of(Comando.MOVER_BAIXO), interpretador.interpretar("s"));
        assertEquals(Optional.of(Comando.MOVER_ESQUERDA), interpretador.interpretar("a"));
        assertEquals(Optional.of(Comando.MOVER_DIREITA), interpretador.interpretar("d"));
        assertEquals(Optional.of(Comando.EMBARCAR), interpretador.interpretar("c"));
        assertEquals(Optional.of(Comando.ABORTAR), interpretador.interpretar("q"));
    }

    @Test
    @DisplayName("maiúsculas também valem")
    void aceitaMaiusculas() {
        assertEquals(Optional.of(Comando.MOVER_CIMA), interpretador.interpretar("W"));
        assertEquals(Optional.of(Comando.ABORTAR), interpretador.interpretar("Q"));
    }

    @Test
    @DisplayName("só o primeiro caractere conta: \"wasd\" vale \"w\" (como no jogo original)")
    void soOPrimeiroCaractere() {
        assertEquals(Optional.of(Comando.MOVER_CIMA), interpretador.interpretar("wasd"));
        assertEquals(Optional.of(Comando.EMBARCAR), interpretador.interpretar("cxyz"));
    }

    @Test
    @DisplayName("texto vazio, nulo ou desconhecido não é comando")
    void invalidos() {
        assertTrue(interpretador.interpretar("").isEmpty());
        assertTrue(interpretador.interpretar(null).isEmpty());
        assertTrue(interpretador.interpretar("x").isEmpty());
        assertTrue(interpretador.interpretar("1").isEmpty());
        assertTrue(interpretador.interpretar(" w").isEmpty());
    }
}
