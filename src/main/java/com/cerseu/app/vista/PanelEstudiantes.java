package com.cerseu.app.vista;

import com.cerseu.app.dao.EstudianteDAO;
import com.cerseu.app.modelo.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelEstudiantes extends JPanel {

    private final EstudianteDAO dao = new EstudianteDAO();
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Nombre completo", "DNI", "Correo", "Teléfono"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtDni = new JTextField(10);
    private final JTextField txtCorreo = new JTextField(20);
    private final JTextField txtTelefono = new JTextField(10);
    private int idSeleccionado = -1;

    public PanelEstudiantes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        agregarCampo(form, gbc, 0, "Nombre completo:", txtNombre);
        agregarCampo(form, gbc, 1, "DNI / documento:", txtDni);
        agregarCampo(form, gbc, 2, "Correo electrónico:", txtCorreo);
        agregarCampo(form, gbc, 3, "Teléfono:", txtTelefono);

        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.NORTH);
        norte.add(botones, BorderLayout.SOUTH);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(ev -> cargarSeleccion());
        btnAgregar.addActionListener(ev -> agregar());
        btnActualizar.addActionListener(ev -> actualizar());
        btnEliminar.addActionListener(ev -> eliminar());
        btnLimpiar.addActionListener(ev -> limpiar());

        cargarTabla();
    }

    private void agregarCampo(JPanel form, GridBagConstraints gbc, int fila, String etiqueta, JTextField campo) {
        gbc.gridx = 0; gbc.gridy = fila;
        form.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        form.add(campo, gbc);
    }

    public void refrescarDatos() {
        cargarTabla();
    }

    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Estudiante> lista = dao.listarTodos();
            for (Estudiante e : lista) {
                modeloTabla.addRow(new Object[]{e.getIdEstudiante(), e.getNombreCompleto(),
                        e.getDocumentoIdentidad(), e.getCorreoElectronico(), e.getTelefono()});
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
            txtDni.setText((String) modeloTabla.getValueAt(fila, 2));
            txtCorreo.setText((String) modeloTabla.getValueAt(fila, 3));
            Object tel = modeloTabla.getValueAt(fila, 4);
            txtTelefono.setText(tel == null ? "" : tel.toString());
        }
    }

    private Estudiante leerFormulario() {
        Estudiante e = new Estudiante();
        e.setIdEstudiante(idSeleccionado);
        e.setNombreCompleto(txtNombre.getText().trim());
        e.setDocumentoIdentidad(txtDni.getText().trim());
        e.setCorreoElectronico(txtCorreo.getText().trim());
        e.setTelefono(txtTelefono.getText().trim());
        return e;
    }

    private boolean validar() {
        if (txtNombre.getText().isBlank() || txtDni.getText().isBlank() || txtCorreo.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Nombre, DNI y correo son obligatorios.");
            return false;
        }
        return true;
    }

    private void agregar() {
        if (!validar()) return;
        try {
            dao.insertar(leerFormulario());
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
        if (!validar()) return;
        try {
            dao.actualizar(leerFormulario());
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
                "¿Eliminar este estudiante? Esto puede fallar si tiene matrículas asociadas.",
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
        txtDni.setText("");
        txtCorreo.setText("");
        txtTelefono.setText("");
        tabla.clearSelection();
    }

    private void mostrarError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error de base de datos:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
