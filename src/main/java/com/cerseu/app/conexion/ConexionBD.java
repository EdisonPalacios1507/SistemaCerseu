package com.cerseu.app.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase de acceso a la conexión JDBC con la base de datos MySQL "arguedas_cerseu".
 *
 * Se recomienda usar el usuario de aplicación creado en el script SQL
 * (app_arguedas) en lugar del usuario administrador, ya que solo tiene
 * permisos de SELECT/INSERT/UPDATE/DELETE/EXECUTE, siguiendo el principio
 * de menor privilegio definido en el propio script de la base de datos.
 */
public class ConexionBD {

    // ---- Ajusta estos datos según tu entorno local de MySQL ----
    private static final String HOST = "localhost";
    private static final String PUERTO = "3306";
    private static final String BASE_DATOS = "arguedas_cerseu";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BASE_DATOS
            + "?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true&characterEncoding=UTF-8";

    private static final String USUARIO = "root";
    private static final String CLAVE = "123456";

    private static Connection conexion;

    private ConexionBD() {
        // Clase utilitaria: no se instancia
    }

    /**
     * Devuelve una conexión activa (patrón singleton). Si la conexión se
     * cerró o nunca se abrió, crea una nueva.
     */
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

    /** Cierra la conexión activa, si existe. */
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Prueba rápida de conectividad, usada al iniciar la aplicación. */
    public static boolean probarConexion() {
        try {
            return obtenerConexion() != null;
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }
}
