package com.cerseu.app.vista;

import com.cerseu.app.dao.OdsDAO;
import com.cerseu.app.modelo.Ods;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelODS extends JPanel {

    private final OdsDAO dao = new OdsDAO();
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Categoría (Objetivo de Desarrollo Sostenible)"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private final JTextField txtNombre = new JTextField(30);
    private int idSeleccionado = -1;

    public PanelODS() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        form.add(new JLabel("Nombre de la categoría:"));
        form.add(txtNombre);

        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(form);
        norte.add(botones);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(ev -> cargarSeleccion());
        btnAgregar.addActionListener(ev -> agregar());
        btnActualizar.addActionListener(ev -> actualizar());
        btnEliminar.addActionListener(ev -> eliminar());
        btnLimpiar.addActionListener(ev -> limpiar());

        cargarTabla();
    }

    /** Vuelve a leer la tabla desde la base de datos. Se llama al entrar a esta pestaña. */
    public void refrescarDatos() {
        cargarTabla();
    }

    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Ods> lista = dao.listarTodos();
            for (Ods o : lista) {
                modeloTabla.addRow(new Object[]{o.getIdOds(), o.getNombreCategoria()});
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
        }
    }

    private void agregar() {
        if (txtNombre.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Ingresa el nombre de la categoría.");
            return;
        }
        try {
            Ods o = new Ods();
            o.setNombreCategoria(txtNombre.getText().trim());
            dao.insertar(o);
            cargarTabla();
            limpiar();
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void actualizar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila de la tabla primero.");
            return;
        }
        try {
            Ods o = new Ods(idSeleccionado, txtNombre.getText().trim());
            dao.actualizar(o);
            cargarTabla();
            limpiar();
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila de la tabla primero.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Eliminar esta categoría ODS? Esto puede fallar si algún curso la usa.",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;
        try {
            dao.eliminar(idSeleccionado);
            cargarTabla();
            limpiar();
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        txtNombre.setText("");
        tabla.clearSelection();
    }

    private void mostrarError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error de base de datos:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
