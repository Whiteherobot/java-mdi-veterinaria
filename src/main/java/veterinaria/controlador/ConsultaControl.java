package veterinaria.controlador;

import veterinaria.modelo.Consulta;
import veterinaria.modelo.Mascota;
import veterinaria.modelo.Propietario;
import veterinaria.servicio.Clinica;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ConsultaControl {

    private final Clinica clinica;

    public ConsultaControl(Clinica clinica) {
        this.clinica = clinica;
    }

    /** Registra una consulta nueva si el número es 0; si no, modifica la existente. */
    public Consulta guardar(int numero, Mascota mascota, String fecha, String motivo, String diagnostico,
                            String peso, String costo) {
        if (mascota == null) {
            throw new IllegalArgumentException("Seleccione la mascota");
        }
        Consulta datos = new Consulta(numero, mascota.codigo(), Fechas.leer(fecha, "de la consulta"), motivo,
                diagnostico, Fechas.numero(peso, "El peso"), Fechas.numero(costo, "El costo"));
        return numero == 0 ? clinica.registrarConsulta(datos) : clinica.modificarConsulta(datos);
    }

    public Consulta eliminar(int numero) {
        return clinica.eliminarConsulta(numero);
    }

    public List<Consulta> listar() {
        return clinica.consultas();
    }

    public List<Mascota> mascotas() {
        return clinica.mascotas();
    }

    public Mascota mascota(int codigo) {
        return clinica.mascota(codigo);
    }

    public Propietario propietarioDe(Mascota mascota) {
        return clinica.propietario(mascota.codigoPropietario());
    }

    public Optional<BigDecimal> variacionPeso(Consulta consulta) {
        return clinica.variacionPeso(consulta);
    }

    public String hoy() {
        return Fechas.formato(clinica.hoy());
    }
}
