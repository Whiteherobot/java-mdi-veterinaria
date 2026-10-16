package veterinaria.modelo;

import java.time.LocalDate;
import java.time.Period;

/**
 * Mascota registrada. Siempre pertenece a un propietario.
 */
public record Mascota(int codigo, String nombre, Especie especie, String raza, LocalDate nacimiento,
                      int codigoPropietario) {

    public Mascota {
        nombre = Validar.requerido(nombre, "Ingrese el nombre de la mascota");
        if (especie == null) {
            throw new IllegalArgumentException("Seleccione la especie");
        }
        raza = Validar.opcional(raza);
        if (nacimiento == null) {
            throw new IllegalArgumentException("Ingrese la fecha de nacimiento");
        }
    }

    public Mascota conCodigo(int nuevoCodigo) {
        return new Mascota(nuevoCodigo, nombre, especie, raza, nacimiento, codigoPropietario);
    }

    /** Edad en años y meses, por ejemplo "2 años 3 meses" o "5 meses". */
    public String edad(LocalDate hoy) {
        Period p = Period.between(nacimiento, hoy);
        String meses = p.getMonths() + (p.getMonths() == 1 ? " mes" : " meses");
        if (p.getYears() == 0) {
            return meses;
        }
        String anios = p.getYears() + (p.getYears() == 1 ? " año" : " años");
        return p.getMonths() == 0 ? anios : anios + " " + meses;
    }

    @Override
    public String toString() {
        return nombre + " (" + especie + ")";
    }
}
