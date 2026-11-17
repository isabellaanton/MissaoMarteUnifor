package br.unifor.missaomarte.aplicacao;

import br.unifor.missaomarte.dominio.Comando;

import java.util.Optional;

/** Traduz o texto digitado em um {@link Comando} do domínio (vazio se não for reconhecido). */
@FunctionalInterface
public interface InterpretadorDeComandos {
    Optional<Comando> interpretar(String texto);
}
