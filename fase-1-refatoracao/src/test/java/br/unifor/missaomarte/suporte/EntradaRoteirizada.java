package br.unifor.missaomarte.suporte;

import br.unifor.missaomarte.aplicacao.EntradaDoJogador;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/** Entrada falsa: devolve respostas pré-definidas e registra as perguntas feitas. */
public class EntradaRoteirizada implements EntradaDoJogador {
    private final Deque<String> respostas = new ArrayDeque<>();
    private final List<String> perguntasFeitas = new ArrayList<>();

    public EntradaRoteirizada(String... respostas) {
        for (String r : respostas) {
            this.respostas.add(r);
        }
    }

    @Override
    public Optional<String> perguntar(String pergunta) {
        perguntasFeitas.add(pergunta);
        return Optional.ofNullable(respostas.poll());
    }

    @Override
    public boolean temMaisEntrada() {
        return !respostas.isEmpty();
    }

    public List<String> getPerguntasFeitas() {
        return perguntasFeitas;
    }
}
