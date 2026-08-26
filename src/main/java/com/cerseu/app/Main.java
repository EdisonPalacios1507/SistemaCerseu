package com.cerseu.app;

import com.cerseu.app.conexion.ConexionBD;
import com.cerseu.app.vista.MainFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            
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
