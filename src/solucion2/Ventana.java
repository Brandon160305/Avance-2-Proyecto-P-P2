// Ventana.java
package solucion2;

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
    private JButton btnCerrarSesion;

    private DetectorRiesgoService2 detector;

    public Ventana(DetectorRiesgoService2 detector) {
        this.detector = detector;

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

        btnCerrarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarSesion();
            }
        });

        actualizarComboFechas();
    }

    private void cerrarSesion() {
        JFrame ventanaActual = (JFrame) SwingUtilities.getWindowAncestor(principal);
        ventanaActual.dispose();

        Login login = new Login();
        JFrame frameLogin = new JFrame("Login");
        frameLogin.setContentPane(login.getPrincipal());
        frameLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameLogin.pack();
        frameLogin.setLocationRelativeTo(null);
        frameLogin.setVisible(true);
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

        boolean ok = detector.agregarFecha(fecha.trim());

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

        // Validaciones UI mínimas
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

        // CAMBIO: registrarEstado devuelve error si algo sale mal
        String error = detector.registrarEstado(id.trim(), fecha, estado, nota);

        if (error != null) {
            JOptionPane.showMessageDialog(principal,
                    error,
                    "No se pudo registrar",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

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

    public void aplicarRol(String rol) {
        if (rol.equals("STAFF")) {
            tabbedPane1.setSelectedIndex(1);
            tabbedPane1.setEnabledAt(0, false);
            tabbedPane1.setEnabledAt(1, true);
        } else {
            tabbedPane1.setSelectedIndex(0);
            tabbedPane1.setEnabledAt(1, false);
            tabbedPane1.setEnabledAt(0, true);
        }
    }

    public void mostrar() {
        JFrame frame = new JFrame("MindSpace - Solución 2");
        frame.setContentPane(principal);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
