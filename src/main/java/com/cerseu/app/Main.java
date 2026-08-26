package com.cerseu.app;

import com.cerseu.app.conexion.ConexionBD;
import com.cerseu.app.vista.MainFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Look & Feel Nimbus: viene incluido en el JDK, no requiere ninguna
        // librería externa (a diferencia de FlatLaf).
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            // Si Nimbus no está disponible por algún motivo, se usa el Look & Feel por defecto
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            if (!ConexionBD.probarConexion()) {
                JOptionPane.showMessageDialog(null,
                        "No se pudo conectar a la base de datos 'arguedas_cerseu'.\n"
                                + "Verifica que MySQL esté encendido y revisa los datos en ConexionBD.java "
                                + "(host, puerto, usuario y contraseña).",
                        "Error de conexión", JOptionPane.ERROR_MESSAGE);
            }
            new MainFrame().setVisible(true);
        });
    }
}
