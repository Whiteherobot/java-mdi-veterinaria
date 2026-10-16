package veterinaria.modelo;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MascotaTest {

    private Mascota nacidaEl(LocalDate fecha) {
        return new Mascota(1, "Michi", Especie.GATO, "", fecha, 1);
    }

    @Test
    void calculaLaEdadEnAniosYMeses() {
        LocalDate hoy = LocalDate.of(2022, 6, 23);
        assertEquals("5 meses", nacidaEl(LocalDate.of(2022, 1, 10)).edad(hoy));
        assertEquals("1 año", nacidaEl(LocalDate.of(2021, 6, 23)).edad(hoy));
        assertEquals("3 años 1 mes", nacidaEl(LocalDate.of(2019, 5, 1)).edad(hoy));
        assertEquals("0 meses", nacidaEl(LocalDate.of(2022, 6, 20)).edad(hoy));
    }

    @Test
    void validaLosDatos() {
        assertThrows(IllegalArgumentException.class, () -> new Mascota(0, " ", Especie.GATO, "", LocalDate.now(), 1));
        assertThrows(IllegalArgumentException.class, () -> new Propietario(0, "Ana", "Torres", "", ""));
        assertThrows(IllegalArgumentException.class, () -> new Consulta(0, 1, LocalDate.now(), "Control", "",
                BigDecimal.ZERO, BigDecimal.ONE));
    }
}
