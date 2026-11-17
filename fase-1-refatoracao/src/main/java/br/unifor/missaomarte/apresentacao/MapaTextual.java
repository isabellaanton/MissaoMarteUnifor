package br.unifor.missaomarte.apresentacao;

import br.unifor.missaomarte.dominio.Asteroide;
import br.unifor.missaomarte.dominio.Inimigo;
import br.unifor.missaomarte.dominio.Limites;
import br.unifor.missaomarte.dominio.Missao;
import br.unifor.missaomarte.dominio.Passageiro;
import br.unifor.missaomarte.dominio.TipoPassageiro;

/**
 * Desenha o mapa em texto. Função pura (devolve {@code String}), por isso é testável
 * sem capturar a saída padrão. O símbolo do passageiro vem do {@link TipoPassageiro},
 * sem {@code instanceof} nas subclasses.
 */
public final class MapaTextual {
    private static final char NAVE = '@';
    private static final char ASTEROIDE = '#';
    private static final char INIMIGO = 'X';
    private static final char POUSO = 'L';
    private static final char VAZIO = '.';

    private MapaTextual() {
    }

    public static String renderizar(Missao missao, Limites limites, int pontuacao, String piloto) {
        StringBuilder sb = new StringBuilder();
        sb.append(System.lineSeparator());
        sb.append(String.format("Mapa da Missão (Pontos: %d) - Piloto: %s%n", pontuacao, piloto));

        sb.append("    ");
        for (int x = limites.minX(); x <= limites.maxX(); x++) {
            sb.append(String.format(" %2d", x));
        }
        sb.append(System.lineSeparator());
        sb.append("    ");
        for (int x = limites.minX(); x <= limites.maxX(); x++) {
            sb.append(" __");
        }
        sb.append(System.lineSeparator());

        for (int y = limites.minY(); y <= limites.maxY(); y++) {
            sb.append(String.format("%3d|", y));
            for (int x = limites.minX(); x <= limites.maxX(); x++) {
                sb.append(String.format(" %2c", simboloEm(missao, x, y)));
            }
            sb.append(System.lineSeparator());
        }

        sb.append("Legenda: @=Nave, P=Professor, E=Engenheiro, T=Astronauta, #=Asteroide, X=Inimigo, L=Plataforma de Pouso, .=Vazio")
                .append(System.lineSeparator());
        sb.append("Comandos: w/s/a/d (mover), c (embarcar), q (sair)").append(System.lineSeparator());
        sb.append("Passageiros na superfície marciana:").append(System.lineSeparator());
        for (Passageiro p : missao.getPassageiros()) {
            sb.append(String.format(" - %s (%s) em (%d,%d)%n",
                    p.getNome(), p.getTipo().getRotulo(), p.getX(), p.getY()));
        }
        return sb.toString();
    }

    /** Ordem de precedência na célula: nave, passageiro, asteroide, inimigo, pouso, vazio. */
    static char simboloEm(Missao missao, int x, int y) {
        if (missao.getNave().estaEm(x, y)) {
            return NAVE;
        }
        for (Passageiro p : missao.getPassageiros()) {
            if (p.estaEm(x, y)) {
                return simbolo(p.getTipo());
            }
        }
        for (Asteroide a : missao.getAsteroides()) {
            if (a.estaEm(x, y)) {
                return ASTEROIDE;
            }
        }
        for (Inimigo i : missao.getInimigos()) {
            if (i.estaEm(x, y)) {
                return INIMIGO;
            }
        }
        if (x == Missao.POUSO_X && y == Missao.POUSO_Y) {
            return POUSO;
        }
        return VAZIO;
    }

    private static char simbolo(TipoPassageiro tipo) {
        switch (tipo) {
            case ENGENHEIRO: return 'E';
            case ASTRONAUTA: return 'T';
            case PROFESSOR:
            default: return 'P';
        }
    }
}
