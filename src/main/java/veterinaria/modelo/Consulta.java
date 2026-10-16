package veterinaria.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Consulta veterinaria de una mascota, con el peso registrado ese día.
 */
public record Consulta(int numero, int codigoMascota, LocalDate fecha, String motivo, String diagnostico,
                       BigDecimal pesoKg, BigDecimal costo) {

    public Consulta {
        if (fecha == null) {
            throw new IllegalArgumentException("Ingrese la fecha de la consulta");
        }
        motivo = Validar.requerido(motivo, "Ingrese el motivo de la consulta");
        diagnostico = Validar.opcional(diagnostico);
        if (pesoKg == null || pesoKg.signum() <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor que cero");
        }
        if (costo == null || costo.signum() < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }
        pesoKg = pesoKg.setScale(2, RoundingMode.HALF_UP);
        costo = costo.setScale(2, RoundingMode.HALF_UP);
    }

    public Consulta conNumero(int nuevoNumero) {
        return new Consulta(nuevoNumero, codigoMascota, fecha, motivo, diagnostico, pesoKg, costo);
    }
}
