package veterinaria.servicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import veterinaria.modelo.Consulta;
import veterinaria.modelo.Especie;
import veterinaria.modelo.Mascota;
import veterinaria.modelo.Propietario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClinicaTest {

    private Clinica c;
    private LocalDate hoy;
    private int ana;
    private Mascota michi;

    @BeforeEach
    void preparar() {
        c = new Clinica(new RelojManual());
        hoy = c.hoy();
        ana = c.registrarPropietario(new Propietario(0, "Ana", "Torres", "0991234567", "")).codigo();
        michi = c.registrarMascota(new Mascota(0, "Michi", Especie.GATO, "", hoy.minusYears(2), ana));
    }

    private Consulta consulta(LocalDate fecha, String peso) {
        return new Consulta(0, michi.codigo(), fecha, "Control", "", new BigDecimal(peso), BigDecimal.TEN);
    }

    @Test
    void laMascotaNecesitaUnPropietarioExistente() {
        assertThrows(IllegalArgumentException.class,
                () -> c.registrarMascota(new Mascota(0, "Rocky", Especie.PERRO, "", hoy, 99)));
        assertThrows(IllegalArgumentException.class,
                () -> c.registrarMascota(new Mascota(0, "Rocky", Especie.PERRO, "", hoy.plusDays(1), ana)));
    }

    @Test
    void validaLasFechasDeLaConsulta() {
        assertThrows(IllegalArgumentException.class, () -> c.registrarConsulta(consulta(hoy.plusDays(1), "4")));
        assertThrows(IllegalArgumentException.class, () -> c.registrarConsulta(consulta(hoy.minusYears(3), "4")));
        c.registrarConsulta(consulta(hoy, "4"));
    }

    @Test
    void historialOrdenadoYVariacionDePeso() {
        Consulta reciente = c.registrarConsulta(consulta(hoy.minusDays(5), "4.85"));
        Consulta antigua = c.registrarConsulta(consulta(hoy.minusMonths(6), "4.20"));
        assertEquals(List.of(antigua, reciente), c.historial(michi.codigo()));
        assertEquals(Optional.empty(), c.variacionPeso(antigua));
        assertEquals(new BigDecimal("0.65"), c.variacionPeso(reciente).orElseThrow());
    }

    @Test
    void integridadEntreRegistros() {
        Consulta hace6Meses = c.registrarConsulta(consulta(hoy.minusMonths(6), "4"));
        assertThrows(IllegalStateException.class, () -> c.eliminarPropietario(ana));
        assertThrows(IllegalStateException.class, () -> c.eliminarMascota(michi.codigo()));
        Mascota nacidaDespuesDeLaConsulta =
                new Mascota(michi.codigo(), "Michi", Especie.GATO, "", hoy.minusMonths(1), ana);
        assertThrows(IllegalArgumentException.class, () -> c.modificarMascota(nacidaDespuesDeLaConsulta));

        c.eliminarConsulta(hace6Meses.numero());
        c.eliminarMascota(michi.codigo());
        c.eliminarPropietario(ana);
        assertEquals(0, c.propietarios().size());
    }
}
