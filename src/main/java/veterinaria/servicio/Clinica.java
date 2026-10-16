package veterinaria.servicio;

import veterinaria.modelo.Consulta;
import veterinaria.modelo.Mascota;
import veterinaria.modelo.Propietario;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Reglas de la clínica que relacionan propietarios, mascotas y consultas.
 */
public class Clinica {

    private final Repositorio<Propietario> propietarios = new Repositorio<>("un propietario", Propietario::codigo);
    private final Repositorio<Mascota> mascotas = new Repositorio<>("una mascota", Mascota::codigo);
    private final Repositorio<Consulta> consultas = new Repositorio<>("una consulta", Consulta::numero);
    private final Clock reloj;

    public Clinica(Clock reloj) {
        this.reloj = reloj;
    }

    public LocalDate hoy() {
        return LocalDate.now(reloj);
    }

    // Propietarios

    public Propietario registrarPropietario(Propietario datos) {
        return propietarios.crear(datos.conCodigo(propietarios.siguienteCodigo()));
    }

    public Propietario modificarPropietario(Propietario propietario) {
        return propietarios.actualizar(propietario);
    }

    public Propietario eliminarPropietario(int codigo) {
        long cantidad = mascotasDe(codigo).size();
        if (cantidad > 0) {
            throw new IllegalStateException("No se puede eliminar: tiene " + cantidad + " mascota(s) registrada(s).");
        }
        return propietarios.eliminar(codigo);
    }

    public List<Propietario> propietarios() {
        return propietarios.listar();
    }

    public Propietario propietario(int codigo) {
        return propietarios.obtener(codigo);
    }

    // Mascotas

    public Mascota registrarMascota(Mascota datos) {
        validarMascota(datos);
        return mascotas.crear(datos.conCodigo(mascotas.siguienteCodigo()));
    }

    public Mascota modificarMascota(Mascota mascota) {
        validarMascota(mascota);
        boolean consultaAnterior = historial(mascota.codigo()).stream()
                .anyMatch(c -> c.fecha().isBefore(mascota.nacimiento()));
        if (consultaAnterior) {
            throw new IllegalArgumentException("Hay consultas anteriores a esa fecha de nacimiento.");
        }
        return mascotas.actualizar(mascota);
    }

    public Mascota eliminarMascota(int codigo) {
        if (!historial(codigo).isEmpty()) {
            throw new IllegalStateException("No se puede eliminar: la mascota tiene consultas en su historial.");
        }
        return mascotas.eliminar(codigo);
    }

    public List<Mascota> mascotas() {
        return mascotas.listar();
    }

    public List<Mascota> mascotasDe(int codigoPropietario) {
        return mascotas.listar().stream().filter(m -> m.codigoPropietario() == codigoPropietario).toList();
    }

    public Mascota mascota(int codigo) {
        return mascotas.obtener(codigo);
    }

    // Consultas

    public Consulta registrarConsulta(Consulta datos) {
        validarConsulta(datos);
        return consultas.crear(datos.conNumero(consultas.siguienteCodigo()));
    }

    public Consulta modificarConsulta(Consulta consulta) {
        validarConsulta(consulta);
        return consultas.actualizar(consulta);
    }

    public Consulta eliminarConsulta(int numero) {
        return consultas.eliminar(numero);
    }

    public List<Consulta> consultas() {
        return consultas.listar();
    }

    /** Consultas de una mascota, de la más antigua a la más reciente. */
    public List<Consulta> historial(int codigoMascota) {
        return consultas.listar().stream()
                .filter(c -> c.codigoMascota() == codigoMascota)
                .sorted(Comparator.comparing(Consulta::fecha).thenComparingInt(Consulta::numero))
                .toList();
    }

    /** Diferencia de peso con la consulta anterior de la misma mascota; vacío si es la primera. */
    public Optional<BigDecimal> variacionPeso(Consulta consulta) {
        List<Consulta> h = historial(consulta.codigoMascota());
        int i = h.indexOf(consulta);
        return i > 0 ? Optional.of(consulta.pesoKg().subtract(h.get(i - 1).pesoKg())) : Optional.empty();
    }

    private void validarMascota(Mascota mascota) {
        propietarios.obtener(mascota.codigoPropietario());
        if (mascota.nacimiento().isAfter(hoy())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }
    }

    private void validarConsulta(Consulta consulta) {
        Mascota mascota = mascotas.obtener(consulta.codigoMascota());
        if (consulta.fecha().isAfter(hoy())) {
            throw new IllegalArgumentException("La fecha de la consulta no puede ser futura");
        }
        if (consulta.fecha().isBefore(mascota.nacimiento())) {
            throw new IllegalArgumentException("La consulta no puede ser anterior al nacimiento de " + mascota.nombre());
        }
    }
}
