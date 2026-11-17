package br.unifor.missaomarte.dominio;

/** Fatos relevantes que acontecem durante um turno e que a apresentação precisa mostrar. */
public enum TipoEvento {
    PASSAGEIRO_EMBARCADO,
    SEM_PASSAGEIRO_NA_POSICAO,
    NAVE_CHEIA,
    MISSAO_ABORTADA,
    COLISAO,
    NAVE_DESTRUIDA,
    PONTUACAO_ZERADA,
    RETORNE_A_PLATAFORMA,
    MISSAO_CUMPRIDA
}
