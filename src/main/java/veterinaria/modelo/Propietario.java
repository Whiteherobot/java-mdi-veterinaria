package veterinaria.modelo;

/**
 * Dueño de una o varias mascotas. El código lo asigna el sistema.
 */
public record Propietario(int codigo, String nombres, String apellidos, String telefono, String correo) {

    public Propietario {
        nombres = Validar.requerido(nombres, "Ingrese los nombres");
        apellidos = Validar.requerido(apellidos, "Ingrese los apellidos");
        telefono = Validar.requerido(telefono, "Ingrese un teléfono de contacto");
        correo = Validar.opcional(correo);
        if (!correo.isEmpty() && !correo.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new IllegalArgumentException("El correo no es válido");
        }
    }

    public Propietario conCodigo(int nuevoCodigo) {
        return new Propietario(nuevoCodigo, nombres, apellidos, telefono, correo);
    }

    public String nombreCompleto() {
        return nombres + " " + apellidos;
    }

    @Override
    public String toString() {
        return nombreCompleto();
    }
}
