package com.cerseu.app.dao;

import com.cerseu.app.conexion.ConexionBD;
import com.cerseu.app.modelo.Matricula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatriculaDAO {

    /**
     * Registra una matrícula invocando el procedimiento almacenado
     * sp_matricular_estudiante, que valida cupos y estado del curso
     * (Regla de negocio 4) directamente en la base de datos.
     * Si el procedimiento lanza un SIGNAL (curso lleno o no habilitado),
     * la SQLException resultante se propaga con el mensaje original.
     */
    public void matricular(int idEstudiante, int idCurso, String periodo) throws SQLException {
        String sql = "{CALL sp_matricular_estudiante(?, ?, ?)}";
        try (CallableStatement cs = ConexionBD.obtenerConexion().prepareCall(sql)) {
            cs.setInt(1, idEstudiante);
            cs.setInt(2, idCurso);
            cs.setString(3, periodo);
            cs.execute();
        }
    }

    public void actualizarEstado(int idMatricula, String nuevoEstado) throws SQLException {
        String sql = "UPDATE Matricula SET estado_matricula = ? WHERE id_matricula = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idMatricula);
            ps.executeUpdate();
        }
    }

    public void eliminar(int idMatricula) throws SQLException {
        String sql = "DELETE FROM Matricula WHERE id_matricula = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, idMatricula);
            ps.executeUpdate();
        }
    }

    public List<Matricula> listarTodas() throws SQLException {
        List<Matricula> lista = new ArrayList<>();
        String sql = "SELECT m.id_matricula, m.id_estudiante, e.nombre_completo AS nombre_estudiante, "
                + "m.id_curso, c.nombre_curso, m.periodo_academico, m.estado_matricula "
                + "FROM Matricula m "
                + "INNER JOIN Estudiante e ON m.id_estudiante = e.id_estudiante "
                + "INNER JOIN Curso c ON m.id_curso = c.id_curso "
                + "ORDER BY m.id_matricula";
        try (Statement st = ConexionBD.obtenerConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Matricula mapear(ResultSet rs) throws SQLException {
        Matricula m = new Matricula();
        m.setIdMatricula(rs.getInt("id_matricula"));
        m.setIdEstudiante(rs.getInt("id_estudiante"));
        m.setNombreEstudiante(rs.getString("nombre_estudiante"));
        m.setIdCurso(rs.getInt("id_curso"));
        m.setNombreCurso(rs.getString("nombre_curso"));
        m.setPeriodoAcademico(rs.getString("periodo_academico"));
        m.setEstadoMatricula(rs.getString("estado_matricula"));
        return m;
    }
}
