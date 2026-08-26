package com.cerseu.app.dao;

import com.cerseu.app.conexion.ConexionBD;
import com.cerseu.app.modelo.Ods;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OdsDAO {

    public void insertar(Ods ods) throws SQLException {
        String sql = "INSERT INTO ODS (nombre_categoria) VALUES (?)";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, ods.getNombreCategoria());
            ps.executeUpdate();
        }
    }

    public void actualizar(Ods ods) throws SQLException {
        String sql = "UPDATE ODS SET nombre_categoria = ? WHERE id_ods = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, ods.getNombreCategoria());
            ps.setInt(2, ods.getIdOds());
            ps.executeUpdate();
        }
    }

    public void eliminar(int idOds) throws SQLException {
        String sql = "DELETE FROM ODS WHERE id_ods = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, idOds);
            ps.executeUpdate();
        }
    }

    public List<Ods> listarTodos() throws SQLException {
        List<Ods> lista = new ArrayList<>();
        String sql = "SELECT id_ods, nombre_categoria FROM ODS ORDER BY id_ods";
        try (Statement st = ConexionBD.obtenerConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Ods(rs.getInt("id_ods"), rs.getString("nombre_categoria")));
            }
        }
        return lista;
    }
}
