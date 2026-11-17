package br.unifor.missaomarte.entrada;

import br.unifor.missaomarte.aplicacao.InterpretadorDeComandos;
import br.unifor.missaomarte.dominio.Comando;

import java.util.Optional;

/**
 * Mapeamento de teclado: só o primeiro caractere importa ("wasd" vale "w"), como no
 * jogo original. w/s/a/d movem, c embarca, q aborta.
 */
public class InterpretadorPorTecla implements InterpretadorDeComandos {

    @Override
    public Optional<Comando> interpretar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return Optional.empty();
        }
        switch (Character.toLowerCase(texto.charAt(0))) {
            case 'w': return Optional.of(Comando.MOVER_CIMA);
            case 's': return Optional.of(Comando.MOVER_BAIXO);
            case 'a': return Optional.of(Comando.MOVER_ESQUERDA);
            case 'd': return Optional.of(Comando.MOVER_DIREITA);
            case 'c': return Optional.of(Comando.EMBARCAR);
            case 'q': return Optional.of(Comando.ABORTAR);
            default: return Optional.empty();
        }
    }
}
