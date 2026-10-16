package veterinaria.servicio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;

/**
 * CRUD en memoria de registros inmutables identificados por un código entero.
 * Modificar reemplaza el registro con el mismo código.
 */
public class Repositorio<T> {

    private final List<T> registros = new ArrayList<>();
    private final ToIntFunction<T> codigo;
    private final String entidad;
    private int ultimoCodigo;

    public Repositorio(String entidad, ToIntFunction<T> codigo) {
        this.entidad = entidad;
        this.codigo = codigo;
    }

    /** Código para un registro nuevo. Nunca se reutiliza, aunque se eliminen registros. */
    public int siguienteCodigo() {
        return ultimoCodigo + 1;
    }

    public T crear(T registro) {
        int c = codigo.applyAsInt(registro);
        if (buscar(c).isPresent()) {
            throw new IllegalArgumentException("Ya existe " + entidad + " con el código " + c);
        }
        registros.add(registro);
        ultimoCodigo = Math.max(ultimoCodigo, c);
        return registro;
    }

    public T actualizar(T registro) {
        int posicion = posicion(codigo.applyAsInt(registro));
        registros.set(posicion, registro);
        return registro;
    }

    public T eliminar(int c) {
        return registros.remove(posicion(c));
    }

    public Optional<T> buscar(int c) {
        return registros.stream().filter(r -> codigo.applyAsInt(r) == c).findFirst();
    }

    public T obtener(int c) {
        return registros.get(posicion(c));
    }

    public List<T> listar() {
        return Collections.unmodifiableList(registros);
    }

    /** Posición del registro; falla si no existe (nunca devuelve una posición inválida). */
    private int posicion(int c) {
        for (int i = 0; i < registros.size(); i++) {
            if (codigo.applyAsInt(registros.get(i)) == c) {
                return i;
            }
        }
        throw new IllegalArgumentException("No existe " + entidad + " con el código " + c);
    }
}
