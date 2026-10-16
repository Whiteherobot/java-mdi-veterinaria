package veterinaria.vista;

import veterinaria.controlador.PropietarioControl;
import veterinaria.modelo.Propietario;

import javax.swing.JTextField;
import java.util.List;

class PanelPropietarios extends PanelCrud<Propietario> {

    final JTextField nombres = new JTextField(14);
    final JTextField apellidos = new JTextField(14);
    final JTextField telefono = new JTextField(10);
    final JTextField correo = new JTextField(14);

    private final PropietarioControl control;

    PanelPropietarios(PropietarioControl control, Mensajes mensajes) {
        super("Propietario", new ModeloTabla<Propietario>()
                .numero("Código", Propietario::codigo)
                .columna("Nombres", Propietario::nombres)
                .columna("Apellidos", Propietario::apellidos)
                .columna("Teléfono", Propietario::telefono)
                .columna("Correo", Propietario::correo)
                .numero("Mascotas", p -> control.cantidadMascotas(p.codigo())), mensajes);
        this.control = control;
        agregarCampo("Nombres *", nombres);
        agregarCampo("Teléfono *", telefono);
        agregarCampo("Apellidos *", apellidos);
        agregarCampo("Correo", correo);
        refrescar();
        limpiar();
    }

    @Override
    protected List<Propietario> listar() {
        return control.listar();
    }

    @Override
    protected int codigo(Propietario p) {
        return p.codigo();
    }

    @Override
    protected void mostrarEnFormulario(Propietario p) {
        nombres.setText(p.nombres());
        apellidos.setText(p.apellidos());
        telefono.setText(p.telefono());
        correo.setText(p.correo());
    }

    @Override
    protected void vaciarFormulario() {
        for (JTextField campo : new JTextField[] {nombres, apellidos, telefono, correo}) {
            campo.setText("");
        }
    }

    @Override
    protected Propietario guardar(int codigo) {
        return control.guardar(codigo, nombres.getText(), apellidos.getText(), telefono.getText(), correo.getText());
    }

    @Override
    protected void eliminar(int codigo) {
        control.eliminar(codigo);
    }

    @Override
    protected String descripcion(Propietario p) {
        return "a " + p.nombreCompleto();
    }
}
