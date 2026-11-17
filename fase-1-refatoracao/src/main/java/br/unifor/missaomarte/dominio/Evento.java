package br.unifor.missaomarte.dominio;

/**
 * Resultado observável de uma regra. As regras <b>descrevem</b> o que aconteceu; a
 * camada de apresentação decide <b>como</b> mostrar (texto no console, som, animação).
 * É isso que permite trocar o console por libGDX sem tocar na {@link Partida}.
 *
 * @param tipo  o que aconteceu
 * @param nome  nome do passageiro (só em {@link TipoEvento#PASSAGEIRO_EMBARCADO})
 * @param valor pontos ganhos ou vidas restantes, conforme o tipo
 */
public record Evento(TipoEvento tipo, String nome, int valor) {

    public static Evento semDetalhe(TipoEvento tipo) {
        return new Evento(tipo, null, 0);
    }

    public static Evento passageiroEmbarcado(String nome, int pontos) {
        return new Evento(TipoEvento.PASSAGEIRO_EMBARCADO, nome, pontos);
    }

    public static Evento colisao(int vidasRestantes) {
        return new Evento(TipoEvento.COLISAO, null, vidasRestantes);
    }
}
