package veterinaria.controlador;

import veterinaria.modelo.Propietario;
import veterinaria.servicio.Clinica;

import java.util.List;

public class PropietarioControl {

    private final Clinica clinica;

    public PropietarioControl(Clinica clinica) {
        this.clinica = clinica;
    }

    /** Registra un propietario nuevo si el código es 0; si no, modifica el existente. */
    public Propietario guardar(int codigo, String nombres, String apellidos, String telefono, String correo) {
        Propietario datos = new Propietario(codigo, nombres, apellidos, telefono, correo);
        return codigo == 0 ? clinica.registrarPropietario(datos) : clinica.modificarPropietario(datos);
    }

    public Propietario eliminar(int codigo) {
        return clinica.eliminarPropietario(codigo);
    }

    public List<Propietario> listar() {
        return clinica.propietarios();
    }

    public int cantidadMascotas(int codigo) {
        return clinica.mascotasDe(codigo).size();
    }
}
