package br.unifor.missaomarte.suporte;

/** Utilidades de texto para os testes. */
public final class Textos {
    private Textos() {
    }

    /** Normaliza o fim de linha para que o teste passe igual no Windows e no Linux. */
    public static String normalizar(String texto) {
        return texto.replace("\r\n", "\n");
    }
}
