package br.unifor.missaomarte.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Nave implements Posicionavel {
    public static final int VIDAS_INICIAIS = 3;

    private final String id;
    private final int capacidade;
    private final List<Passageiro> passageiros = new ArrayList<>();
    private int x;
    private int y;
    private int vidas;

    public Nave(String id, int capacidade) {
        this.id = id;
        this.capacidade = capacidade;
        this.vidas = VIDAS_INICIAIS;
        this.x = 0;
        this.y = 0;
    }

    public String getId() {
        return id;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public int getVidas() {
        return vidas;
    }

    /** Visão somente leitura: quem embarca passageiros é {@link #embarcar(Passageiro)}. */
    public List<Passageiro> getPassageiros() {
        return Collections.unmodifiableList(passageiros);
    }

    /** Anda uma célula na direção pedida, sem sair do mapa (nesse caso, não se move). */
    public void mover(Direcao direcao, Limites limites) {
        switch (direcao) {
            case CIMA:
                if (y > limites.minY()) y--;
                break;
            case BAIXO:
                if (y < limites.maxY()) y++;
                break;
            case ESQUERDA:
                if (x > limites.minX()) x--;
                break;
            case DIREITA:
                if (x < limites.maxX()) x++;
                break;
            default:
                break;
        }
    }

    public boolean embarcar(Passageiro passageiro) {
        if (passageiros.size() < capacidade) {
            passageiros.add(passageiro);
            return true;
        }
        return false;
    }

    public void perderVida() {
        if (vidas > 0) {
            vidas--;
        }
    }
}
