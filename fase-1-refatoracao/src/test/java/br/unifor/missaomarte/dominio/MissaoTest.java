package br.unifor.missaomarte.dominio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissaoTest {
    private final Limites limites = Limites.quadrado(3);

    private Missao missaoVazia(int capacidade) {
        return new Missao(new Nave("T-1", capacidade));
    }

    @Test
    @DisplayName("colide quando a nave está na mesma célula de um asteroide")
    void colisaoComAsteroide() {
        Missao m = missaoVazia(5);
        m.addAsteroide(new Asteroide(1, 0));
        assertFalse(m.verificaColisao());
        m.getNave().mover(Direcao.DIREITA, limites);
        assertTrue(m.verificaColisao());
    }

    @Test
    @DisplayName("colide quando a nave está na mesma célula de um inimigo")
    void colisaoComInimigo() {
        Missao m = missaoVazia(5);
        m.addInimigo(new Inimigo(0, 0));
        assertTrue(m.verificaColisao());
    }

    @Test
    @DisplayName("sem obstáculo na célula da nave não há colisão")
    void semColisao() {
        Missao m = missaoVazia(5);
        m.addAsteroide(new Asteroide(2, 2));
        m.addInimigo(new Inimigo(-2, 1));
        assertFalse(m.verificaColisao());
    }

    @Test
    @DisplayName("passageiro só é encontrado na célula da nave")
    void passageiroNaPosicaoDaNave() {
        Missao m = missaoVazia(5);
        Passageiro p = new Engenheiro("Eng", 1, 0);
        m.addPassageiro(p);
        assertNull(m.passageiroNaPosicaoDaNave());
        m.getNave().mover(Direcao.DIREITA, limites);
        assertSame(p, m.passageiroNaPosicaoDaNave());
    }

    @Test
    @DisplayName("embarcar move o passageiro da superfície para a nave")
    void embarcarPassageiro() {
        Missao m = missaoVazia(5);
        m.addPassageiro(new Professor("Prof", 1, 0));
        m.getNave().mover(Direcao.DIREITA, limites);

        assertTrue(m.embarcarPassageiroNaPosicao());

        assertEquals(0, m.passageirosRestantes());
        assertEquals(1, m.getNave().getPassageiros().size());
        assertTrue(m.todosEmbarcados());
    }

    @Test
    @DisplayName("sem passageiro na célula, embarcar não faz nada")
    void embarcarSemPassageiro() {
        Missao m = missaoVazia(5);
        m.addPassageiro(new Professor("Prof", 2, 2));
        assertFalse(m.embarcarPassageiroNaPosicao());
        assertEquals(1, m.passageirosRestantes());
        assertFalse(m.todosEmbarcados());
    }

    @Test
    @DisplayName("com a nave cheia o passageiro continua na superfície")
    void embarcarComNaveCheia() {
        Missao m = missaoVazia(1);
        m.getNave().embarcar(new Professor("Ocupante", 0, 0));
        m.addPassageiro(new Professor("Prof", 1, 0));
        m.getNave().mover(Direcao.DIREITA, limites);

        assertFalse(m.embarcarPassageiroNaPosicao());
        assertEquals(1, m.passageirosRestantes());
    }

    @Test
    @DisplayName("a plataforma de pouso é a célula (0,0)")
    void plataformaDePouso() {
        Missao m = missaoVazia(5);
        assertTrue(m.naveNaPlataformaDePouso());
        m.getNave().mover(Direcao.BAIXO, limites);
        assertFalse(m.naveNaPlataformaDePouso());
    }

    @Test
    @DisplayName("uma célula está ocupada pela nave, por passageiro, asteroide ou inimigo")
    void posicaoOcupada() {
        Missao m = missaoVazia(5);
        m.addPassageiro(new Professor("P", 1, 1));
        m.addAsteroide(new Asteroide(2, 2));
        m.addInimigo(new Inimigo(3, 3));

        assertTrue(m.posicaoOcupada(0, 0));
        assertTrue(m.posicaoOcupada(1, 1));
        assertTrue(m.posicaoOcupada(2, 2));
        assertTrue(m.posicaoOcupada(3, 3));
        assertFalse(m.posicaoOcupada(-1, -1));
    }

    @Test
    @DisplayName("as coleções expostas são somente leitura")
    void colecoesSomenteLeitura() {
        Missao m = missaoVazia(5);
        assertNotNull(m.getNave());
        assertThrows(UnsupportedOperationException.class, () -> m.getPassageiros().add(new Professor("P", 1, 1)));
        assertThrows(UnsupportedOperationException.class, () -> m.getAsteroides().add(new Asteroide(1, 1)));
        assertThrows(UnsupportedOperationException.class, () -> m.getInimigos().add(new Inimigo(1, 1)));
    }
}
