package com.cerseu.app.dao;

import com.cerseu.app.conexion.ConexionBD;
import com.cerseu.app.modelo.Asistencia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {

    public void registrar(Asistencia a) throws SQLException {
        String sql = "INSERT INTO Asistencia (id_matricula, fecha_sesion, estado_asistencia) VALUES (?, ?, ?)";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, a.getIdMatricula());
            ps.setDate(2, Date.valueOf(a.getFechaSesion()));
            ps.setString(3, a.getEstadoAsistencia());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idAsistencia) throws SQLException {
        String sql = "DELETE FROM Asistencia WHERE id_asistencia = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, idAsistencia);
            ps.executeUpdate();
        }
    }

    /**
     * Calcula el porcentaje de asistencia (sesiones "Presente" / total de sesiones)
     * de una matrícula, útil para llenar automáticamente el Acta de Notas.
     */
    public double calcularPorcentajeAsistencia(int idMatricula) throws SQLException {
        String sql = "SELECT "
                + "SUM(CASE WHEN estado_asistencia = 'Presente' THEN 1 ELSE 0 END) AS presentes, "
                + "COUNT(*) AS total "
                + "FROM Asistencia WHERE id_matricula = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, idMatricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    if (total == 0) return 0.0;
                    return (rs.getInt("presentes") * 100.0) / total;
                }
            }
        }
        return 0.0;
    }

    public List<Asistencia> listarTodas() throws SQLException {
        List<Asistencia> lista = new ArrayList<>();
        String sql = "SELECT a.id_asistencia, a.id_matricula, e.nombre_completo AS nombre_estudiante, "
                + "c.nombre_curso, a.fecha_sesion, a.estado_asistencia "
                + "FROM Asistencia a "
                + "INNER JOIN Matricula m ON a.id_matricula = m.id_matricula "
                + "INNER JOIN Estudiante e ON m.id_estudiante = e.id_estudiante "
                + "INNER JOIN Curso c ON m.id_curso = c.id_curso "
                + "ORDER BY a.id_asistencia";
        try (Statement st = ConexionBD.obtenerConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Asistencia a = new Asistencia();
                a.setIdAsistencia(rs.getInt("id_asistencia"));
                a.setIdMatricula(rs.getInt("id_matricula"));
                a.setNombreEstudiante(rs.getString("nombre_estudiante"));
                a.setNombreCurso(rs.getString("nombre_curso"));
                a.setFechaSesion(rs.getDate("fecha_sesion").toLocalDate());
                a.setEstadoAsistencia(rs.getString("estado_asistencia"));
                lista.add(a);
            }
        }
        return lista;
    }
}
