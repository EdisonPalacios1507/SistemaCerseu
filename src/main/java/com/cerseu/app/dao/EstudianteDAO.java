package com.cerseu.app.dao;

import com.cerseu.app.conexion.ConexionBD;
import com.cerseu.app.modelo.Estudiante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {

    public void insertar(Estudiante e) throws SQLException {
        String sql = "INSERT INTO Estudiante (nombre_completo, documento_identidad, correo_electronico, telefono) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, e.getNombreCompleto());
            ps.setString(2, e.getDocumentoIdentidad());
            ps.setString(3, e.getCorreoElectronico());
            ps.setString(4, e.getTelefono());
            ps.executeUpdate();
        }
    }

    public void actualizar(Estudiante e) throws SQLException {
        String sql = "UPDATE Estudiante SET nombre_completo = ?, documento_identidad = ?, "
                + "correo_electronico = ?, telefono = ? WHERE id_estudiante = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, e.getNombreCompleto());
            ps.setString(2, e.getDocumentoIdentidad());
            ps.setString(3, e.getCorreoElectronico());
            ps.setString(4, e.getTelefono());
            ps.setInt(5, e.getIdEstudiante());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idEstudiante) throws SQLException {
        String sql = "DELETE FROM Estudiante WHERE id_estudiante = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.executeUpdate();
        }
    }

    public List<Estudiante> listarTodos() throws SQLException {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT id_estudiante, nombre_completo, documento_identidad, correo_electronico, telefono "
                + "FROM Estudiante ORDER BY id_estudiante";
        try (Statement st = ConexionBD.obtenerConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Estudiante mapear(ResultSet rs) throws SQLException {
        Estudiante e = new Estudiante();
        e.setIdEstudiante(rs.getInt("id_estudiante"));
        e.setNombreCompleto(rs.getString("nombre_completo"));
        e.setDocumentoIdentidad(rs.getString("documento_identidad"));
        e.setCorreoElectronico(rs.getString("correo_electronico"));
        e.setTelefono(rs.getString("telefono"));
        return e;
    }
}
