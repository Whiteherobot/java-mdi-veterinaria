package veterinaria.vista;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Pantalla CRUD común: formulario arriba, tabla con buscador abajo.
 * Seleccionar una fila la carga en el formulario para modificarla o eliminarla;
 * "Nuevo" limpia el formulario para registrar otro.
 *
 * @param <T> registro que se administra
 */
abstract class PanelCrud<T> extends JPanel {

    final JTextField buscar = new JTextField(18);
    final JButton nuevo = new JButton("Nuevo");
    final JButton guardar = new JButton("Guardar");
    final JButton eliminar = new JButton("Eliminar");
    final JLabel modo = new JLabel();
    final ModeloTabla<T> modelo;
    final JTable tabla;

    protected final Mensajes mensajes;
    private final JPanel formulario = new JPanel(new GridBagLayout());
    private final TableRowSorter<ModeloTabla<T>> orden;
    private int campos;
    /** Código del registro cargado en el formulario; 0 si es uno nuevo. */
    private int codigoActual;

    PanelCrud(String titulo, ModeloTabla<T> modelo, Mensajes mensajes) {
        super(new BorderLayout(0, 8));
        this.modelo = modelo;
        this.mensajes = mensajes;
        this.tabla = modelo.crearTabla();
        this.orden = new TableRowSorter<>(modelo);
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        tabla.setRowSorter(orden);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarSeleccion();
            }
        });

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        acciones.add(modo);
        acciones.add(nuevo);
        acciones.add(guardar);
        acciones.add(eliminar);
        nuevo.addActionListener(e -> limpiar());
        guardar.addActionListener(e -> guardar());
        eliminar.addActionListener(e -> eliminar());

        JPanel superior = new JPanel(new BorderLayout());
        superior.setBorder(BorderFactory.createTitledBorder(titulo));
        superior.add(formulario, BorderLayout.CENTER);
        superior.add(acciones, BorderLayout.PAGE_END);

        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEADING));
        busqueda.add(new JLabel("Buscar"));
        busqueda.add(buscar);
        buscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }
        });

        JPanel listado = new JPanel(new BorderLayout());
        listado.add(busqueda, BorderLayout.PAGE_START);
        listado.add(new JScrollPane(tabla), BorderLayout.CENTER);

        add(superior, BorderLayout.PAGE_START);
        add(listado, BorderLayout.CENTER);
    }

    // Lo que define cada pantalla

    protected abstract List<T> listar();

    protected abstract int codigo(T registro);

    protected abstract void mostrarEnFormulario(T registro);

    protected abstract void vaciarFormulario();

    /** Guarda con los datos del formulario; código 0 significa registro nuevo. */
    protected abstract T guardar(int codigo);

    protected abstract void eliminar(int codigo);

    protected abstract String descripcion(T registro);

    // Comportamiento común

    protected final void agregarCampo(String etiqueta, JComponent campo) {
        int fila = campos / 2;
        int columna = (campos % 2) * 2;
        campos++;
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila;
        c.gridx = columna;
        c.insets = new Insets(3, 6, 3, 6);
        c.anchor = GridBagConstraints.LINE_END;
        formulario.add(new JLabel(etiqueta), c);
        c.gridx = columna + 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        formulario.add(campo, c);
    }

    void refrescar() {
        modelo.mostrar(listar());
        if (codigoActual != 0 && listar().stream().noneMatch(r -> codigo(r) == codigoActual)) {
            limpiar();
        }
    }

    void limpiar() {
        codigoActual = 0;
        tabla.clearSelection();
        vaciarFormulario();
        modo.setText("Registro nuevo");
        eliminar.setEnabled(false);
    }

    private void cargarSeleccion() {
        T registro = modelo.fila(tabla.convertRowIndexToModel(tabla.getSelectedRow()));
        codigoActual = codigo(registro);
        mostrarEnFormulario(registro);
        modo.setText("Editando " + descripcion(registro));
        eliminar.setEnabled(true);
    }

    private void guardar() {
        try {
            boolean nuevoRegistro = codigoActual == 0;
            T guardado = guardar(codigoActual);
            refrescar();
            seleccionar(codigo(guardado));
            mensajes.aviso((nuevoRegistro ? "Se registró " : "Se modificó ") + descripcion(guardado) + ".");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mensajes.error(ex.getMessage());
        }
    }

    private void eliminar() {
        if (codigoActual == 0) {
            return;
        }
        T registro = listar().stream().filter(r -> codigo(r) == codigoActual).findFirst().orElse(null);
        if (registro == null || !mensajes.confirmar("¿Eliminar " + descripcion(registro) + "?")) {
            return;
        }
        try {
            eliminar(codigoActual);
            limpiar();
            refrescar();
            mensajes.aviso("Se eliminó " + descripcion(registro) + ".");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mensajes.error(ex.getMessage());
        }
    }

    private void seleccionar(int codigo) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if (codigo(modelo.fila(i)) == codigo) {
                int vista = tabla.convertRowIndexToView(i);
                if (vista >= 0) {
                    tabla.setRowSelectionInterval(vista, vista);
                }
                return;
            }
        }
    }

    private void filtrar() {
        String texto = buscar.getText().strip();
        orden.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
    }
}
