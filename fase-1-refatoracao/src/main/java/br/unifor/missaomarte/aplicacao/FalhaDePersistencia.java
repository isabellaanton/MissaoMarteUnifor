package br.unifor.missaomarte.aplicacao;

/** Erro de gravação/remoção do ranking. A mensagem é a do erro de E/S original. */
public class FalhaDePersistencia extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FalhaDePersistencia(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
