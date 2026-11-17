package br.unifor.missaomarte.dominio;

import br.unifor.missaomarte.suporte.Cenarios;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InimigoTest {
    private final Limites limites = Limites.quadrado(2);

    @Test
    @DisplayName("sorteio 0 = x+1, 1 = x-1, 2 = y+1, 3 = y-1")
    void cadaSorteioMoveEmUmaDirecao() {
        Inimigo i = new Inimigo(0, 0);
        i.mover(Cenarios.sorteioFixo(0), limites);
        assertEquals(1, i.getX());
        assertEquals(0, i.getY());

        i.mover(Cenarios.sorteioFixo(1), limites);
        assertEquals(0, i.getX());

        i.mover(Cenarios.sorteioFixo(2), limites);
        assertEquals(1, i.getY());

        i.mover(Cenarios.sorteioFixo(3), limites);
        assertEquals(0, i.getY());
    }

    @Test
    @DisplayName("o inimigo não sai do mapa: na borda o passo é ignorado")
    void respeitaOsLimites() {
        Inimigo naDireita = new Inimigo(2, 0);
        naDireita.mover(Cenarios.sorteioFixo(0), limites);
        assertEquals(2, naDireita.getX());

        Inimigo naEsquerda = new Inimigo(-2, 0);
        naEsquerda.mover(Cenarios.sorteioFixo(1), limites);
        assertEquals(-2, naEsquerda.getX());

        Inimigo embaixo = new Inimigo(0, 2);
        embaixo.mover(Cenarios.sorteioFixo(2), limites);
        assertEquals(2, embaixo.getY());

        Inimigo emCima = new Inimigo(0, -2);
        emCima.mover(Cenarios.sorteioFixo(3), limites);
        assertEquals(-2, emCima.getY());
    }
}
