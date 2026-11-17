package br.unifor.missaomarte.apresentacao;

import br.unifor.missaomarte.dominio.Asteroide;
import br.unifor.missaomarte.dominio.Astronauta;
import br.unifor.missaomarte.dominio.Direcao;
import br.unifor.missaomarte.dominio.Engenheiro;
import br.unifor.missaomarte.dominio.Inimigo;
import br.unifor.missaomarte.dominio.Limites;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Nave;
import br.unifor.missaomarte.dominio.Professor;
import br.unifor.missaomarte.suporte.Textos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapaTextualTest {
    private final Limites limites = Limites.quadrado(1);

    private Missao missaoDeExemplo() {
        Missao m = new Missao(new Nave("T-1", 5));
        m.addPassageiro(new Engenheiro("Eng. Rosa", 1, 1));
        m.addPassageiro(new Astronauta("Ast. Maria", -1, -1));
        m.addPassageiro(new Professor("Dr. Silva", 1, -1));
        m.addAsteroide(new Asteroide(-1, 1));
        m.addInimigo(new Inimigo(0, 1));
        return m;
    }

    @Test
    @DisplayName("desenha cabeçalho, grade com símbolos, legenda e lista de passageiros")
    void mapaCompleto() {
        Missao m = missaoDeExemplo();
        m.getNave().mover(Direcao.DIREITA, limites);   // nave em (1,0); a plataforma 'L' fica visível em (0,0)

        String esperado = "\n"
                + "Mapa da Missão (Pontos: 28) - Piloto: Ana\n"
                + "     -1  0  1\n"
                + "     __ __ __\n"
                + " -1|  T  .  P\n"
                + "  0|  .  L  @\n"
                + "  1|  #  X  E\n"
                + "Legenda: @=Nave, P=Professor, E=Engenheiro, T=Astronauta, #=Asteroide, X=Inimigo, L=Plataforma de Pouso, .=Vazio\n"
                + "Comandos: w/s/a/d (mover), c (embarcar), q (sair)\n"
                + "Passageiros na superfície marciana:\n"
                + " - Eng. Rosa (Engenheiro) em (1,1)\n"
                + " - Ast. Maria (Astronauta) em (-1,-1)\n"
                + " - Dr. Silva (Professor) em (1,-1)\n";

        assertEquals(esperado, Textos.normalizar(MapaTextual.renderizar(m, limites, 28, "Ana")));
    }

    @Test
    @DisplayName("a nave tem prioridade sobre a plataforma de pouso na célula (0,0)")
    void naveSobreAPlataforma() {
        Missao m = missaoDeExemplo();
        assertEquals('@', MapaTextual.simboloEm(m, 0, 0));
    }

    @Test
    @DisplayName("precedência: passageiro vence asteroide e inimigo; asteroide vence inimigo")
    void precedenciaDosSimbolos() {
        Missao m = new Missao(new Nave("T-1", 5));
        m.addPassageiro(new Professor("P", 1, 1));
        m.addAsteroide(new Asteroide(1, 1));
        m.addInimigo(new Inimigo(1, 1));
        m.addAsteroide(new Asteroide(-1, -1));
        m.addInimigo(new Inimigo(-1, -1));

        assertEquals('P', MapaTextual.simboloEm(m, 1, 1));
        assertEquals('#', MapaTextual.simboloEm(m, -1, -1));
        assertEquals('.', MapaTextual.simboloEm(m, 1, -1));
    }

    @Test
    @DisplayName("cada tipo de passageiro tem o seu símbolo: P, E e T")
    void simboloPorTipo() {
        Missao m = new Missao(new Nave("T-1", 5));
        m.addPassageiro(new Professor("a", 1, 0));
        m.addPassageiro(new Engenheiro("b", 0, 1));
        m.addPassageiro(new Astronauta("c", 1, 1));
        assertEquals('P', MapaTextual.simboloEm(m, 1, 0));
        assertEquals('E', MapaTextual.simboloEm(m, 0, 1));
        assertEquals('T', MapaTextual.simboloEm(m, 1, 1));
    }
}
