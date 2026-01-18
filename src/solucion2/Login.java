// Login.java (sin cambios funcionales, solo lo dejo completo por copiar/pegar)
package solucion2;

import javax.swing.*;

public class Login {

    private JPanel principal;
    private JTextField txtUser;
    private JPasswordField txtpass;
    private JButton btnLogin;
    private JLabel juser;
    private JLabel Jcontraseña;

    public static DetectorRiesgoService2 detectorGlobal = new DetectorRiesgoService2();

    public Login() {
        btnLogin.addActionListener(e -> iniciarSesion());
    }

    private void iniciarSesion() {
        String user = txtUser.getText().trim();
        String pass = new String(txtpass.getPassword()).trim();

        boolean esStaff = user.equalsIgnoreCase("staff") && pass.equals("1234");
        boolean esEstudiante = user.equalsIgnoreCase("estudiante") && pass.equals("1234");

        if (!esStaff && !esEstudiante) {
            JOptionPane.showMessageDialog(principal,
                    "Usuario o contraseña incorrectos.\n\nEjemplos:\n- estudiante / 1234\n- staff / 1234",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Ventana v = new Ventana(Login.detectorGlobal);
        if (esStaff) v.aplicarRol("STAFF");
        else v.aplicarRol("ESTUDIANTE");

        SwingUtilities.getWindowAncestor(principal).dispose();
        v.mostrar();
    }

    public void mostrar() {
        JFrame frame = new JFrame("MindSpace - Login");
        frame.setContentPane(principal);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().mostrar());
    }

    public JPanel getPrincipal() {
        return principal;
    }
}
