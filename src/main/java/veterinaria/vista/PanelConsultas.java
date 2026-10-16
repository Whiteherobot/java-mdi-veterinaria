package veterinaria.vista;

import veterinaria.controlador.ConsultaControl;
import veterinaria.controlador.Fechas;
import veterinaria.modelo.Consulta;
import veterinaria.modelo.Mascota;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.math.BigDecimal;
import java.util.List;

/**
 * Consultas de todas las mascotas. Escribiendo el nombre de una mascota en el
 * buscador se ve su historial.
 */
class PanelConsultas extends PanelCrud<Consulta> {

    final JComboBox<Mascota> mascota = new JComboBox<>();
    final JTextField fecha = new JTextField(9);
    final JTextField motivo = new JTextField(16);
    final JTextField diagnostico = new JTextField(16);
    final JTextField peso = new JTextField(6);
    final JTextField costo = new JTextField(6);

    private final ConsultaControl control;

    PanelConsultas(ConsultaControl control, Mensajes mensajes) {
        super("Consulta", new ModeloTabla<Consulta>()
                .numero("N.º", Consulta::numero)
                .columna("Fecha", c -> Fechas.formato(c.fecha()))
                .columna("Mascota", c -> control.mascota(c.codigoMascota()).nombre())
                .columna("Propietario", c -> control.propietarioDe(control.mascota(c.codigoMascota())).nombreCompleto())
                .columna("Motivo", Consulta::motivo)
                .columna("Diagnóstico", Consulta::diagnostico)
                .numero("Peso (kg)", Consulta::pesoKg)
                .numero("Variación", c -> control.variacionPeso(c).map(PanelConsultas::variacion).orElse(""))
                .numero("Costo", c -> "$" + c.costo()), mensajes);
        this.control = control;
        agregarCampo("Mascota *", mascota);
        agregarCampo("Fecha (dd/mm/aaaa) *", fecha);
        agregarCampo("Motivo *", motivo);
        agregarCampo("Peso (kg) *", peso);
        agregarCampo("Diagnóstico", diagnostico);
        agregarCampo("Costo *", costo);
        refrescar();
        limpiar();
    }

    static String variacion(BigDecimal kg) {
        return (kg.signum() > 0 ? "+" : "") + kg.toPlainString() + " kg";
    }

    @Override
    void refrescar() {
        Object elegida = mascota.getSelectedItem();
        mascota.setModel(new DefaultComboBoxModel<>(control.mascotas().toArray(Mascota[]::new)));
        if (elegida != null) {
            mascota.setSelectedItem(elegida);
        }
        super.refrescar();
    }

    @Override
    protected List<Consulta> listar() {
        return control.listar();
    }

    @Override
    protected int codigo(Consulta c) {
        return c.numero();
    }

    @Override
    protected void mostrarEnFormulario(Consulta c) {
        mascota.setSelectedItem(control.mascota(c.codigoMascota()));
        fecha.setText(Fechas.formato(c.fecha()));
        motivo.setText(c.motivo());
        diagnostico.setText(c.diagnostico());
        peso.setText(c.pesoKg().toPlainString());
        costo.setText(c.costo().toPlainString());
    }

    @Override
    protected void vaciarFormulario() {
        fecha.setText(control.hoy());
        for (JTextField campo : new JTextField[] {motivo, diagnostico, peso, costo}) {
            campo.setText("");
        }
    }

    @Override
    protected Consulta guardar(int numero) {
        return control.guardar(numero, (Mascota) mascota.getSelectedItem(), fecha.getText(), motivo.getText(),
                diagnostico.getText(), peso.getText(), costo.getText());
    }

    @Override
    protected void eliminar(int numero) {
        control.eliminar(numero);
    }

    @Override
    protected String descripcion(Consulta c) {
        return "la consulta " + c.numero() + " de " + control.mascota(c.codigoMascota()).nombre();
    }
}
