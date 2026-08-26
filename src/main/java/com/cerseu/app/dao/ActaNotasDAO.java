package com.cerseu.app.dao;

import com.cerseu.app.conexion.ConexionBD;
import com.cerseu.app.modelo.ActaNotas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActaNotasDAO {

    /**
     * Inserta un acta de notas. NO se envía "estado_final": ese campo lo calcula
     * automáticamente el TRIGGER trg_calcular_estado_final (regla de asistencia
     * mínima 70% y nota mínima aprobatoria 11), por lo que siempre se debe
     * recargar la fila después de insertar para ver el resultado real.
     */
    public void insertar(ActaNotas acta) throws SQLException {
        String sql = "INSERT INTO Acta_Notas (id_matricula, nota_final, porcentaje_asistencia, esta_cerrada) "
                + "VALUES (?, ?, ?, FALSE)";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, acta.getIdMatricula());
            ps.setInt(2, acta.getNotaFinal());
            ps.setBigDecimal(3, acta.getPorcentajeAsistencia());
            ps.executeUpdate();
        }
    }

    /**
     * Intenta actualizar la nota de un acta. Si el acta ya está cerrada, el
     * TRIGGER trg_bloquear_acta_cerrada lanzará un SIGNAL 45000 (Regla 13) y
     * la SQLException resultante debe mostrarse tal cual al usuario.
     */
    public void actualizarNota(int idActa, int nuevaNota, java.math.BigDecimal nuevoPorcentaje) throws SQLException {
        String sql = "UPDATE Acta_Notas SET nota_final = ?, porcentaje_asistencia = ? WHERE id_acta = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, nuevaNota);
            ps.setBigDecimal(2, nuevoPorcentaje);
            ps.setInt(3, idActa);
            ps.executeUpdate();
        }
    }

    /** Cierra oficialmente el acta (esta_cerrada = TRUE); esta operación sí está permitida. */
    public void cerrarActa(int idActa) throws SQLException {
        String sql = "UPDATE Acta_Notas SET esta_cerrada = TRUE WHERE id_acta = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, idActa);
            ps.executeUpdate();
        }
    }

    public List<ActaNotas> listarTodas() throws SQLException {
        List<ActaNotas> lista = new ArrayList<>();
        String sql = "SELECT an.id_acta, an.id_matricula, e.nombre_completo AS nombre_estudiante, "
                + "c.nombre_curso, an.nota_final, an.porcentaje_asistencia, an.estado_final, an.esta_cerrada "
                + "FROM Acta_Notas an "
                + "INNER JOIN Matricula m ON an.id_matricula = m.id_matricula "
                + "INNER JOIN Estudiante e ON m.id_estudiante = e.id_estudiante "
                + "INNER JOIN Curso c ON m.id_curso = c.id_curso "
                + "ORDER BY an.id_acta";
        try (Statement st = ConexionBD.obtenerConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                ActaNotas a = new ActaNotas();
                a.setIdActa(rs.getInt("id_acta"));
                a.setIdMatricula(rs.getInt("id_matricula"));
                a.setNombreEstudiante(rs.getString("nombre_estudiante"));
                a.setNombreCurso(rs.getString("nombre_curso"));
                a.setNotaFinal(rs.getInt("nota_final"));
                a.setPorcentajeAsistencia(rs.getBigDecimal("porcentaje_asistencia"));
                a.setEstadoFinal(rs.getString("estado_final"));
                a.setEstaCerrada(rs.getBoolean("esta_cerrada"));
                lista.add(a);
            }
        }
        return lista;
    }
}
