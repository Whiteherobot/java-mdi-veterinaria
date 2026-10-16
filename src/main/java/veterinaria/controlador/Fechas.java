package veterinaria.controlador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Conversión de los textos que escribe el usuario.
 */
public final class Fechas {

    public static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private Fechas() {
    }

    static LocalDate leer(String texto, String cual) {
        try {
            return LocalDate.parse(texto.strip(), FORMATO);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("La fecha " + cual + " no es válida (use dd/mm/aaaa)");
        }
    }

    static BigDecimal numero(String texto, String campo) {
        try {
            return new BigDecimal(texto.strip().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(campo + " debe ser un número");
        }
    }

    public static String formato(LocalDate fecha) {
        return fecha.format(FORMATO);
    }
}
