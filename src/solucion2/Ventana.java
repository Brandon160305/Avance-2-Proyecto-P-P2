package solucion2;
//primer commit Eduardo Larrea
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Ventana {
    private JPanel principal;
    private JTabbedPane tabbedPane1;
    private JTextField txtID;
    private JTextField txtFecha;
    private JButton btnIngresarFecha;
    private JComboBox cboFecha;
    private JComboBox cboEstado;
    private JTextArea txtNota;
    private JButton btnGuardarEstado;
    private JList lstEstados;
    private JList lstCasosRiesgos;
    private JButton btnAtenderSiguiente;
    private JButton btnRefrescarCasos;

    private DetectorRiesgoService2 detector;

    public Ventana() {


        detector = new DetectorRiesgoService2();




        btnIngresarFecha.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ingresarFecha();
            }
        });


        btnGuardarEstado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarEstado();
            }
        });


        btnRefrescarCasos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarListaCasos();
            }
        });


        btnAtenderSiguiente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atenderSiguienteCaso();
            }
        });
    }


    private void ingresarFecha() {
        String fecha = txtFecha.getText();

        if (fecha == null || fecha.trim().isEmpty()) {
            JOptionPane.showMessageDialog(principal,
                    "Ingresa una fecha (YYYY-MM-DD).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean ok = detector.agregarFecha(fecha);

        if (!ok) {
            JOptionPane.showMessageDialog(principal,
                    "La fecha ingresada NO es válida.\nEjemplo: 2025-11-29",
                    "Fecha inválida",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        txtFecha.setText("");
        actualizarComboFechas();
    }

    private void actualizarComboFechas() {
        List<String> fechas = detector.getFechasDisponibles();
        cboFecha.removeAllItems();
        for (String f : fechas) {
            cboFecha.addItem(f);
        }
    }


    private void guardarEstado() {

        String id = txtID.getText();
        Object fechaObj = cboFecha.getSelectedItem();
        Object estadoObj = cboEstado.getSelectedItem();
        String nota = txtNota.getText();

        if (id == null || id.trim().isEmpty()) {
            JOptionPane.showMessageDialog(principal,
                    "Ingresa el ID del estudiante.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (fechaObj == null) {
            JOptionPane.showMessageDialog(principal,
                    "Selecciona una fecha válida.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (estadoObj == null) {
            JOptionPane.showMessageDialog(principal,
                    "Selecciona un estado de ánimo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fecha = fechaObj.toString();
        String estado = estadoObj.toString();

        detector.registrarEstado(id.trim(), fecha, estado, nota);

        txtNota.setText("");
        actualizarListaEstados();


        CasoRiesgo2 ultimo = detector.getUltimoCasoDetectado();

        if (ultimo != null) {
            int totalCasos = detector.getCasosPendientesSnapshot().size();

            JOptionPane.showMessageDialog(principal,
                    "ALERTA DE RIESGO\n\n" +
                            "Estudiante: " + ultimo.getIdEstudiante() + "\n" +
                            "Racha detectada: " + ultimo.getDiasRacha() + " días tristes consecutivos\n" +
                            "Fecha del último día triste: " + ultimo.getUltimaFecha() + "\n\n" +
                            "Casos pendientes en Bienestar Estudiantil: " + totalCasos,
                    "Racha detectada",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarListaEstados() {
        List<RegistroEstadoAnimo2> historial = detector.getHistorial();
        DefaultListModel modelo = new DefaultListModel();

        for (RegistroEstadoAnimo2 r : historial) {
            modelo.addElement(r.toString());
        }

        lstEstados.setModel(modelo);
    }


    private void actualizarListaCasos() {
        List<CasoRiesgo2> casos = detector.getCasosPendientesSnapshot();
        DefaultListModel modelo = new DefaultListModel();

        for (CasoRiesgo2 c : casos) {
            modelo.addElement(c.toString());
        }

        lstCasosRiesgos.setModel(modelo);
    }

    private void atenderSiguienteCaso() {
        CasoRiesgo2 caso = detector.obtenerSiguienteCaso();

        if (caso == null) {
            JOptionPane.showMessageDialog(principal,
                    "No hay casos pendientes.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(principal,
                    "Atendiendo caso:\n" + caso.toString(),
                    "Caso atendido",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        actualizarListaCasos();
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("MindSpace - Solución 2");
        frame.setContentPane(new Ventana().principal);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
