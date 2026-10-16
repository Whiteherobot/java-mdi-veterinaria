package veterinaria.vista;

/**
 * Cómo la vista se comunica con el usuario: errores y confirmaciones en
 * diálogos, avisos de operaciones exitosas en la barra de estado.
 */
interface Mensajes {

    void error(String mensaje);

    boolean confirmar(String pregunta);

    void aviso(String mensaje);
}
