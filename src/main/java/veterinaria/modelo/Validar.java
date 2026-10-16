package veterinaria.modelo;

final class Validar {

    private Validar() {
    }

    static String requerido(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
        return valor.strip();
    }

    static String opcional(String valor) {
        return valor == null ? "" : valor.strip();
    }
}
