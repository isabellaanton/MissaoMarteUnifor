package br.unifor.missaomarte.suporte;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/** Relógio de teste que só anda quando o teste manda. */
public class RelogioManual extends Clock {
    private long millis;

    public RelogioManual(long millisIniciais) {
        this.millis = millisIniciais;
    }

    public void avancar(long milissegundos) {
        millis += milissegundos;
    }

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return Instant.ofEpochMilli(millis);
    }
}
