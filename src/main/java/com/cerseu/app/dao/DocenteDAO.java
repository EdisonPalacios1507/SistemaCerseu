package com.cerseu.app.dao;

import com.cerseu.app.conexion.ConexionBD;
import com.cerseu.app.modelo.Docente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocenteDAO {

    public void insertar(Docente docente) throws SQLException {
        String sql = "INSERT INTO Docente (nombre_completo) VALUES (?)";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, docente.getNombreCompleto());
            ps.executeUpdate();
        }
    }

    public void actualizar(Docente docente) throws SQLException {
        String sql = "UPDATE Docente SET nombre_completo = ? WHERE id_docente = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, docente.getNombreCompleto());
            ps.setInt(2, docente.getIdDocente());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idDocente) throws SQLException {
        String sql = "DELETE FROM Docente WHERE id_docente = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, idDocente);
            ps.executeUpdate();
        }
    }

    public List<Docente> listarTodos() throws SQLException {
        List<Docente> lista = new ArrayList<>();
        String sql = "SELECT id_docente, nombre_completo FROM Docente ORDER BY id_docente";
        try (Statement st = ConexionBD.obtenerConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Docente(rs.getInt("id_docente"), rs.getString("nombre_completo")));
            }
        }
        return lista;
    }
}
