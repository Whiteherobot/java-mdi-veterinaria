package veterinaria.vista;

import veterinaria.controlador.ConsultaControl;
import veterinaria.controlador.MascotaControl;
import veterinaria.controlador.PropietarioControl;
import veterinaria.servicio.Clinica;

import javax.swing.BorderFactory;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.time.Clock;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Ventana principal MDI: un escritorio donde cada módulo se abre como ventana
 * interna desde el menú. Cada módulo se abre una sola vez.
 */
public class VentanaClinica extends JFrame implements Mensajes {

    private final JDesktopPane escritorio = new JDesktopPane();
    private final JLabel estado = new JLabel(" ");
    private final Map<String, JInternalFrame> abiertas = new LinkedHashMap<>();

    public VentanaClinica() {
        super("Clínica veterinaria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Clinica clinica = new Clinica(Clock.systemDefaultZone());
        DatosEjemplo.cargar(clinica);
        PropietarioControl propietarios = new PropietarioControl(clinica);
        MascotaControl mascotas = new MascotaControl(clinica);
        ConsultaControl consultas = new ConsultaControl(clinica);

        JMenu registros = new JMenu("Registros");
        registros.add(item("Propietarios", () -> new PanelPropietarios(propietarios, this)));
        registros.add(item("Mascotas", () -> new PanelMascotas(mascotas, this)));
        registros.add(item("Consultas", () -> new PanelConsultas(consultas, this)));
        JMenuItem cascada = new JMenuItem("Cascada");
        cascada.addActionListener(e -> cascada());
        JMenuItem cerrar = new JMenuItem("Cerrar todas");
        cerrar.addActionListener(e -> abiertas.values().forEach(JInternalFrame::dispose));
        JMenu ventana = new JMenu("Ventana");
        ventana.add(cascada);
        ventana.add(cerrar);
        JMenuBar barra = new JMenuBar();
        barra.add(registros);
        barra.add(ventana);
        setJMenuBar(barra);

        estado.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(escritorio, BorderLayout.CENTER);
        add(estado, BorderLayout.PAGE_END);
        setSize(1000, 680);
        setLocationRelativeTo(null);
        aviso("Abra un módulo desde el menú Registros.");
    }

    private JMenuItem item(String titulo, Supplier<PanelCrud<?>> panel) {
        JMenuItem item = new JMenuItem(titulo);
        item.addActionListener(e -> abrir(titulo, panel));
        return item;
    }

    /** Abre el módulo o, si ya está abierto, lo trae al frente. */
    private void abrir(String titulo, Supplier<PanelCrud<?>> crear) {
        JInternalFrame existente = abiertas.get(titulo);
        if (existente != null) {
            enfocar(existente);
            return;
        }
        PanelCrud<?> panel = crear.get();
        JInternalFrame marco = new JInternalFrame(titulo, true, true, true, true);
        marco.setContentPane(panel);
        marco.setSize(860, 440);
        marco.setLocation(24 * abiertas.size(), 24 * abiertas.size());
        // Al volver a una ventana se recarga, porque otra pudo cambiar los datos.
        marco.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                panel.refrescar();
            }

            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                abiertas.remove(titulo);
            }
        });
        abiertas.put(titulo, marco);
        escritorio.add(marco);
        marco.setVisible(true);
        enfocar(marco);
    }

    private void enfocar(JInternalFrame marco) {
        try {
            marco.setIcon(false);
            marco.setSelected(true);
        } catch (PropertyVetoException ignorada) {
            // La ventana rechazó el cambio; queda como estaba.
        }
        marco.toFront();
    }

    private void cascada() {
        int i = 0;
        for (JInternalFrame marco : abiertas.values()) {
            marco.setLocation(24 * i, 24 * i);
            enfocar(marco);
            i++;
        }
    }

    @Override
    public void error(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "No se pudo completar", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public boolean confirmar(String pregunta) {
        return JOptionPane.showConfirmDialog(this, pregunta, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    @Override
    public void aviso(String mensaje) {
        estado.setText(mensaje);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ignorada) {
                // Se usa el look and feel de Java.
            }
            new VentanaClinica().setVisible(true);
        });
    }
}
