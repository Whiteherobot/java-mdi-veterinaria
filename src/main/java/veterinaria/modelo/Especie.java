package veterinaria.modelo;

public enum Especie {
    PERRO("Perro"),
    GATO("Gato"),
    CONEJO("Conejo"),
    AVE("Ave"),
    OTRA("Otra");

    private final String nombre;

    Especie(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
