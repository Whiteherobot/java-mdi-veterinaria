package veterinaria.vista;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import veterinaria.controlador.ConsultaControl;
import veterinaria.controlador.MascotaControl;
import veterinaria.controlador.PropietarioControl;
import veterinaria.servicio.Clinica;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PanelesTest {

    private final List<String> errores = new ArrayList<>();
    private final List<String> avisos = new ArrayList<>();
    private final Mensajes mensajes = new Mensajes() {
        @Override
        public void error(String m) {
            errores.add(m);
        }

        @Override
        public boolean confirmar(String p) {
            return true;
        }

        @Override
        public void aviso(String m) {
            avisos.add(m);
        }
    };

    private Clinica clinica;

    @BeforeEach
    void preparar() {
        clinica = new Clinica(Clock.systemDefaultZone());
        DatosEjemplo.cargar(clinica);
    }

    @Test
    void registrarUnaMascotaAvisaYApareceEnLaTabla() {
        PanelMascotas panel = new PanelMascotas(new MascotaControl(clinica), mensajes);
        panel.nombre.setText("Luna");
        panel.nacimiento.setText("01/01/2024");
        panel.propietario.setSelectedIndex(1);
        panel.guardar.doClick();
        assertEquals(List.of("Se registró a Luna."), avisos);
        assertEquals(4, panel.modelo.getRowCount());
        assertEquals("Carlos Mendoza Ríos", panel.modelo.getValueAt(3, 5));

        panel.nacimiento.setText("30/02/2024");
        panel.guardar.doClick();
        assertTrue(errores.get(0).startsWith("La fecha de nacimiento no es válida"));
    }

    @Test
    void elHistorialMuestraLaVariacionDePeso() {
        PanelConsultas panel = new PanelConsultas(new ConsultaControl(clinica), mensajes);
        panel.buscar.setText("Michi");
        assertEquals(2, panel.tabla.getRowCount());
        assertEquals("+0.65 kg", panel.modelo.getValueAt(1, 7));
        assertEquals("", panel.modelo.getValueAt(0, 7));
    }

    @Test
    void noEliminaUnPropietarioConMascotas() {
        PanelPropietarios panel = new PanelPropietarios(new PropietarioControl(clinica), mensajes);
        assertEquals(2, panel.modelo.getValueAt(0, 5));
        panel.tabla.setRowSelectionInterval(0, 0);
        panel.eliminar.doClick();
        assertTrue(errores.get(0).contains("2 mascota(s)"));
        assertTrue(avisos.isEmpty());
    }
}
