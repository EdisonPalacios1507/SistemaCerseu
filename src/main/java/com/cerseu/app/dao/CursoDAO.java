package com.cerseu.app.dao;

import com.cerseu.app.conexion.ConexionBD;
import com.cerseu.app.modelo.Curso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {

    public void insertar(Curso c) throws SQLException {
        String sql = "INSERT INTO Curso (nombre_curso, archivo_silabo, capacidad_maxima, costo_base, "
                + "estado_curso, id_ods, id_docente) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, c.getNombreCurso());
            ps.setString(2, c.getArchivoSilabo());
            ps.setInt(3, c.getCapacidadMaxima());
            ps.setBigDecimal(4, c.getCostoBase());
            ps.setString(5, c.getEstadoCurso());
            ps.setInt(6, c.getIdOds());
            if (c.getIdDocente() != null) {
                ps.setInt(7, c.getIdDocente());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.executeUpdate();
        }
    }

    public void actualizar(Curso c) throws SQLException {
        String sql = "UPDATE Curso SET nombre_curso = ?, archivo_silabo = ?, capacidad_maxima = ?, "
                + "costo_base = ?, estado_curso = ?, id_ods = ?, id_docente = ? WHERE id_curso = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, c.getNombreCurso());
            ps.setString(2, c.getArchivoSilabo());
            ps.setInt(3, c.getCapacidadMaxima());
            ps.setBigDecimal(4, c.getCostoBase());
            ps.setString(5, c.getEstadoCurso());
            ps.setInt(6, c.getIdOds());
            if (c.getIdDocente() != null) {
                ps.setInt(7, c.getIdDocente());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setInt(8, c.getIdCurso());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idCurso) throws SQLException {
        String sql = "DELETE FROM Curso WHERE id_curso = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            ps.executeUpdate();
        }
    }

    /** Cantidad de matriculados actualmente en un curso, para un periodo dado (para mostrar vacantes). */
    public int contarMatriculados(int idCurso, String periodo) throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM Matricula WHERE id_curso = ? AND periodo_academico = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            ps.setString(2, periodo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
            }
        }
        return 0;
    }

    public List<Curso> listarTodos() throws SQLException {
        List<Curso> lista = new ArrayList<>();
        String sql = "SELECT c.id_curso, c.nombre_curso, c.archivo_silabo, c.capacidad_maxima, c.costo_base, "
                + "c.estado_curso, c.id_ods, o.nombre_categoria, c.id_docente, d.nombre_completo AS nombre_docente "
                + "FROM Curso c "
                + "INNER JOIN ODS o ON c.id_ods = o.id_ods "
                + "LEFT JOIN Docente d ON c.id_docente = d.id_docente "
                + "ORDER BY c.id_curso";
        try (Statement st = ConexionBD.obtenerConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Curso mapear(ResultSet rs) throws SQLException {
        Curso c = new Curso();
        c.setIdCurso(rs.getInt("id_curso"));
        c.setNombreCurso(rs.getString("nombre_curso"));
        c.setArchivoSilabo(rs.getString("archivo_silabo"));
        c.setCapacidadMaxima(rs.getInt("capacidad_maxima"));
        c.setCostoBase(rs.getBigDecimal("costo_base"));
        c.setEstadoCurso(rs.getString("estado_curso"));
        c.setIdOds(rs.getInt("id_ods"));
        c.setNombreOds(rs.getString("nombre_categoria"));
        int idDocente = rs.getInt("id_docente");
        c.setIdDocente(rs.wasNull() ? null : idDocente);
        c.setNombreDocente(rs.getString("nombre_docente"));
        return c;
    }
}
