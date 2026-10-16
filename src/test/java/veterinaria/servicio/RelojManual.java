package veterinaria.servicio;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/** Reloj que solo avanza cuando la prueba lo pide. */
class RelojManual extends Clock {

    private Instant ahora = Instant.parse("2022-06-23T15:00:00Z");

    void avanzar(Duration d) {
        ahora = ahora.plus(d);
    }

    @Override
    public ZoneId getZone() {
        return ZoneId.of("America/Guayaquil");
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return ahora;
    }
}
