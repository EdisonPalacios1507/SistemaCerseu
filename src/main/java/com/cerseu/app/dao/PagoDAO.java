package com.cerseu.app.dao;

import com.cerseu.app.conexion.ConexionBD;
import com.cerseu.app.modelo.Pago;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    public void registrar(Pago p) throws SQLException {
        String sql = "INSERT INTO Pago (id_matricula, nombre_banco, monto_pagado, fecha_pago, "
                + "archivo_voucher, codigo_recibo, verificacion_veracidad, codigo_moneda, estado_pago) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 'PEN', ?)";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, p.getIdMatricula());
            ps.setString(2, p.getNombreBanco());
            ps.setBigDecimal(3, p.getMontoPagado());
            ps.setDate(4, Date.valueOf(p.getFechaPago()));
            ps.setString(5, p.getArchivoVoucher());
            ps.setString(6, p.getCodigoRecibo());
            ps.setBoolean(7, p.isVerificacionVeracidad());
            ps.setString(8, p.getEstadoPago());
            ps.executeUpdate();
        }
    }

    public void verificarPago(int idPago, String nuevoEstado) throws SQLException {
        String sql = "UPDATE Pago SET verificacion_veracidad = TRUE, estado_pago = ? WHERE id_pago = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPago);
            ps.executeUpdate();
        }
    }

    public void eliminar(int idPago) throws SQLException {
        String sql = "DELETE FROM Pago WHERE id_pago = ?";
        try (PreparedStatement ps = ConexionBD.obtenerConexion().prepareStatement(sql)) {
            ps.setInt(1, idPago);
            ps.executeUpdate();
        }
    }

    public List<Pago> listarTodos() throws SQLException {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT p.id_pago, p.id_matricula, e.nombre_completo AS nombre_estudiante, "
                + "c.nombre_curso, p.nombre_banco, p.monto_pagado, p.fecha_pago, p.archivo_voucher, "
                + "p.codigo_recibo, p.verificacion_veracidad, p.codigo_moneda, p.estado_pago "
                + "FROM Pago p "
                + "INNER JOIN Matricula m ON p.id_matricula = m.id_matricula "
                + "INNER JOIN Estudiante e ON m.id_estudiante = e.id_estudiante "
                + "INNER JOIN Curso c ON m.id_curso = c.id_curso "
                + "ORDER BY p.id_pago";
        try (Statement st = ConexionBD.obtenerConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Pago p = new Pago();
                p.setIdPago(rs.getInt("id_pago"));
                p.setIdMatricula(rs.getInt("id_matricula"));
                p.setNombreEstudiante(rs.getString("nombre_estudiante"));
                p.setNombreCurso(rs.getString("nombre_curso"));
                p.setNombreBanco(rs.getString("nombre_banco"));
                p.setMontoPagado(rs.getBigDecimal("monto_pagado"));
                p.setFechaPago(rs.getDate("fecha_pago").toLocalDate());
                p.setArchivoVoucher(rs.getString("archivo_voucher"));
                p.setCodigoRecibo(rs.getString("codigo_recibo"));
                p.setVerificacionVeracidad(rs.getBoolean("verificacion_veracidad"));
                p.setCodigoMoneda(rs.getString("codigo_moneda"));
                p.setEstadoPago(rs.getString("estado_pago"));
                lista.add(p);
            }
        }
        return lista;
    }
}
