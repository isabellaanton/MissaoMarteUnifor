package br.unifor.missaomarte.dominio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NaveTest {
    private static final Limites LIMITES = Limites.quadrado(2);
    private Nave nave;

    @BeforeEach
    void criarNave() {
        nave = new Nave("A-1", 2);
    }

    @Test
    @DisplayName("a nave nasce na plataforma de pouso (0,0) com 3 vidas e vazia")
    void estadoInicial() {
        assertEquals("A-1", nave.getId());
        assertEquals(0, nave.getX());
        assertEquals(0, nave.getY());
        assertEquals(Nave.VIDAS_INICIAIS, nave.getVidas());
        assertEquals(3, nave.getVidas());
        assertEquals(2, nave.getCapacidade());
        assertTrue(nave.getPassageiros().isEmpty());
    }

    @Test
    @DisplayName("cima diminui y, baixo aumenta y, esquerda diminui x, direita aumenta x")
    void cadaDirecaoMoveUmaCelula() {
        nave.mover(Direcao.CIMA, LIMITES);
        assertEquals(-1, nave.getY());
        nave.mover(Direcao.BAIXO, LIMITES);
        nave.mover(Direcao.BAIXO, LIMITES);
        assertEquals(1, nave.getY());
        nave.mover(Direcao.ESQUERDA, LIMITES);
        assertEquals(-1, nave.getX());
        nave.mover(Direcao.DIREITA, LIMITES);
        nave.mover(Direcao.DIREITA, LIMITES);
        assertEquals(1, nave.getX());
    }

    @Test
    @DisplayName("a nave não ultrapassa nenhuma das quatro bordas do mapa")
    void naoSaiDoMapa() {
        for (int i = 0; i < 10; i++) nave.mover(Direcao.CIMA, LIMITES);
        assertEquals(-2, nave.getY());
        for (int i = 0; i < 10; i++) nave.mover(Direcao.BAIXO, LIMITES);
        assertEquals(2, nave.getY());
        for (int i = 0; i < 10; i++) nave.mover(Direcao.ESQUERDA, LIMITES);
        assertEquals(-2, nave.getX());
        for (int i = 0; i < 10; i++) nave.mover(Direcao.DIREITA, LIMITES);
        assertEquals(2, nave.getX());
    }

    @Test
    @DisplayName("embarcar respeita a capacidade da nave")
    void embarcarRespeitaCapacidade() {
        assertTrue(nave.embarcar(new Professor("P1", 0, 0)));
        assertTrue(nave.embarcar(new Professor("P2", 0, 0)));
        assertFalse(nave.embarcar(new Professor("P3", 0, 0)));
        assertEquals(2, nave.getPassageiros().size());
    }

    @Test
    @DisplayName("perder vida nunca deixa o contador negativo")
    void vidasNaoFicamNegativas() {
        for (int i = 0; i < 5; i++) nave.perderVida();
        assertEquals(0, nave.getVidas());
    }

    @Test
    @DisplayName("a lista de passageiros exposta é somente leitura (encapsulamento)")
    void listaDePassageirosNaoPodeSerAlteradaDeFora() {
        assertThrows(UnsupportedOperationException.class,
                () -> nave.getPassageiros().add(new Professor("P", 0, 0)));
    }
}
