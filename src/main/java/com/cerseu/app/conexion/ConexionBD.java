package com.cerseu.app.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String HOST = "localhost";
    private static final String PUERTO = "3306";
    private static final String BASE_DATOS = "arguedas_cerseu";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS
            + "?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";

    private static final String USUARIO = "root";
    private static final String CLAVE = "123456";

    private static Connection conexion;

    private ConexionBD() {
        
    }

    public static Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("No se encontró el driver JDBC de MySQL (mysql-connector-j). "
                        + "Verifica la dependencia en el pom.xml.", e);
            }
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
        }
        return conexion;
    }


    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean probarConexion() {
        try {
            return obtenerConexion() != null;
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }
}
