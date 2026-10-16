package veterinaria.vista;

import veterinaria.controlador.Fechas;
import veterinaria.controlador.MascotaControl;
import veterinaria.modelo.Especie;
import veterinaria.modelo.Mascota;
import veterinaria.modelo.Propietario;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.util.List;

class PanelMascotas extends PanelCrud<Mascota> {

    final JTextField nombre = new JTextField(12);
    final JComboBox<Especie> especie = new JComboBox<>(Especie.values());
    final JTextField raza = new JTextField(12);
    final JTextField nacimiento = new JTextField(9);
    final JComboBox<Propietario> propietario = new JComboBox<>();

    private final MascotaControl control;

    PanelMascotas(MascotaControl control, Mensajes mensajes) {
        super("Mascota", new ModeloTabla<Mascota>()
                .numero("Código", Mascota::codigo)
                .columna("Nombre", Mascota::nombre)
                .columna("Especie", Mascota::especie)
                .columna("Raza", Mascota::raza)
                .columna("Edad", m -> m.edad(control.hoy()))
                .columna("Propietario", m -> control.propietario(m.codigoPropietario()).nombreCompleto())
                .numero("Consultas", m -> control.cantidadConsultas(m.codigo())), mensajes);
        this.control = control;
        agregarCampo("Nombre *", nombre);
        agregarCampo("Propietario *", propietario);
        agregarCampo("Especie *", especie);
        agregarCampo("Nacimiento (dd/mm/aaaa) *", nacimiento);
        agregarCampo("Raza", raza);
        refrescar();
        limpiar();
    }

    @Override
    void refrescar() {
        Object elegido = propietario.getSelectedItem();
        propietario.setModel(new DefaultComboBoxModel<>(control.propietarios().toArray(Propietario[]::new)));
        if (elegido != null) {
            propietario.setSelectedItem(elegido);
        }
        super.refrescar();
    }

    @Override
    protected List<Mascota> listar() {
        return control.listar();
    }

    @Override
    protected int codigo(Mascota m) {
        return m.codigo();
    }

    @Override
    protected void mostrarEnFormulario(Mascota m) {
        nombre.setText(m.nombre());
        especie.setSelectedItem(m.especie());
        raza.setText(m.raza());
        nacimiento.setText(Fechas.formato(m.nacimiento()));
        propietario.setSelectedItem(control.propietario(m.codigoPropietario()));
    }

    @Override
    protected void vaciarFormulario() {
        nombre.setText("");
        raza.setText("");
        nacimiento.setText("");
        especie.setSelectedIndex(0);
    }

    @Override
    protected Mascota guardar(int codigo) {
        return control.guardar(codigo, nombre.getText(), (Especie) especie.getSelectedItem(), raza.getText(),
                nacimiento.getText(), (Propietario) propietario.getSelectedItem());
    }

    @Override
    protected void eliminar(int codigo) {
        control.eliminar(codigo);
    }

    @Override
    protected String descripcion(Mascota m) {
        return "a " + m.nombre();
    }
}
