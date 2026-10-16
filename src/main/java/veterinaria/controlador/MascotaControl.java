package veterinaria.controlador;

import veterinaria.modelo.Especie;
import veterinaria.modelo.Mascota;
import veterinaria.modelo.Propietario;
import veterinaria.servicio.Clinica;

import java.time.LocalDate;
import java.util.List;

public class MascotaControl {

    private final Clinica clinica;

    public MascotaControl(Clinica clinica) {
        this.clinica = clinica;
    }

    /** Registra una mascota nueva si el código es 0; si no, modifica la existente. */
    public Mascota guardar(int codigo, String nombre, Especie especie, String raza, String nacimiento,
                           Propietario propietario) {
        if (propietario == null) {
            throw new IllegalArgumentException("Seleccione el propietario");
        }
        Mascota datos = new Mascota(codigo, nombre, especie, raza, Fechas.leer(nacimiento, "de nacimiento"),
                propietario.codigo());
        return codigo == 0 ? clinica.registrarMascota(datos) : clinica.modificarMascota(datos);
    }

    public Mascota eliminar(int codigo) {
        return clinica.eliminarMascota(codigo);
    }

    public List<Mascota> listar() {
        return clinica.mascotas();
    }

    public List<Propietario> propietarios() {
        return clinica.propietarios();
    }

    public Propietario propietario(int codigo) {
        return clinica.propietario(codigo);
    }

    public int cantidadConsultas(int codigo) {
        return clinica.historial(codigo).size();
    }

    public LocalDate hoy() {
        return clinica.hoy();
    }
}
