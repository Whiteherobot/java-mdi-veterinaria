package veterinaria.vista;

import veterinaria.modelo.Consulta;
import veterinaria.modelo.Especie;
import veterinaria.modelo.Mascota;
import veterinaria.modelo.Propietario;
import veterinaria.servicio.Clinica;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Registros para probar la aplicación. Las fechas se calculan desde hoy para
 * que las edades y el historial siempre tengan sentido.
 */
final class DatosEjemplo {

    private DatosEjemplo() {
    }

    static void cargar(Clinica c) {
        LocalDate hoy = c.hoy();
        int ana = c.registrarPropietario(new Propietario(0, "Ana Lucía", "Torres Vega", "0991234567",
                "ana.torres@gmail.com")).codigo();
        int carlos = c.registrarPropietario(new Propietario(0, "Carlos", "Mendoza Ríos", "0987654321", "")).codigo();

        int michi = c.registrarMascota(new Mascota(0, "Michi", Especie.GATO, "Mestizo",
                hoy.minusYears(3).minusMonths(2), ana)).codigo();
        int rocky = c.registrarMascota(new Mascota(0, "Rocky", Especie.PERRO, "Labrador",
                hoy.minusMonths(7), carlos)).codigo();
        c.registrarMascota(new Mascota(0, "Copito", Especie.CONEJO, "Cabeza de león", hoy.minusYears(1), ana));

        c.registrarConsulta(consulta(michi, hoy.minusMonths(6), "Vacuna triple felina", "Sano", "4.20", "18"));
        c.registrarConsulta(consulta(michi, hoy.minusDays(10), "Control anual", "Sobrepeso leve", "4.85", "15"));
        c.registrarConsulta(consulta(rocky, hoy.minusMonths(4), "Desparasitación", "", "11.30", "12"));
        c.registrarConsulta(consulta(rocky, hoy.minusMonths(1), "Vacuna antirrábica", "Sano", "17.90", "20"));
    }

    private static Consulta consulta(int mascota, LocalDate fecha, String motivo, String diagnostico,
                                     String peso, String costo) {
        return new Consulta(0, mascota, fecha, motivo, diagnostico, new BigDecimal(peso), new BigDecimal(costo));
    }
}
