package br.unifor.missaomarte.dominio;

import br.unifor.missaomarte.suporte.Cenarios;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumoPartidaTest {

    private static ResumoPartida resumo(int pontuacao, Optional<EntradaRanking> lider) {
        return new ResumoPartida(pontuacao, 6, 3, 4, lider);
    }

    @Test
    @DisplayName("sem ranking não há recorde a bater nem recorde batido")
    void semLider() {
        ResumoPartida r = resumo(70, Optional.empty());
        assertEquals(0, r.recordeAtual());
        assertFalse(r.existeRecordeASerBatido());
        assertFalse(r.bateuRecorde());
    }

    @Test
    @DisplayName("pontuação maior que a do líder bate o recorde")
    void bateuRecorde() {
        ResumoPartida r = resumo(80, Optional.of(Cenarios.entrada("Lider", 70)));
        assertTrue(r.bateuRecorde());
        assertEquals(70, r.recordeAtual());
    }

    @Test
    @DisplayName("pontuação igual ou menor não bate o recorde, mas há recorde a superar")
    void naoBateuRecorde() {
        ResumoPartida igual = resumo(70, Optional.of(Cenarios.entrada("Lider", 70)));
        ResumoPartida menor = resumo(10, Optional.of(Cenarios.entrada("Lider", 70)));
        assertFalse(igual.bateuRecorde());
        assertFalse(menor.bateuRecorde());
        assertTrue(igual.existeRecordeASerBatido());
        assertTrue(menor.existeRecordeASerBatido());
    }
}
