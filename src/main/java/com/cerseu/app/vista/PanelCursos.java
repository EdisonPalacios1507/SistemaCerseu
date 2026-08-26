package com.cerseu.app.vista;

import com.cerseu.app.dao.CursoDAO;
import com.cerseu.app.dao.DocenteDAO;
import com.cerseu.app.dao.OdsDAO;
import com.cerseu.app.modelo.Curso;
import com.cerseu.app.modelo.Docente;
import com.cerseu.app.modelo.Ods;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class PanelCursos extends JPanel {

    private final CursoDAO dao = new CursoDAO();
    private final OdsDAO odsDAO = new OdsDAO();
    private final DocenteDAO docenteDAO = new DocenteDAO();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Curso", "Vacantes", "Costo (S/)", "Estado", "ODS", "Docente"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtSilabo = new JTextField(16);
    private final JTextField txtCapacidad = new JTextField(5);
    private final JTextField txtCosto = new JTextField(7);
    private final JComboBox<String> comboEstado = new JComboBox<>(new String[]{"Habilitado", "Pendiente", "Cerrado"});
    private final JComboBox<Ods> comboOds = new JComboBox<>();
    private final JComboBox<Docente> comboDocente = new JComboBox<>();
    private int idSeleccionado = -1;

    public PanelCursos() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int fila = 0;
        fila = agregarCampo(form, gbc, fila, "Nombre del curso:", txtNombre);
        fila = agregarCampo(form, gbc, fila, "Archivo sílabo:", txtSilabo);
        fila = agregarCampo(form, gbc, fila, "Capacidad máxima:", txtCapacidad);
        fila = agregarCampo(form, gbc, fila, "Costo base (S/):", txtCosto);
        fila = agregarCampo(form, gbc, fila, "Estado:", comboEstado);
        fila = agregarCampo(form, gbc, fila, "ODS asociado:", comboOds);
        agregarCampo(form, gbc, fila, "Docente asignado:", comboDocente);

        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnRefrescar = new JButton("Refrescar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.add(btnAgregar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnRefrescar);

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
        btnRefrescar.addActionListener(ev -> { cargarCombos(); cargarTabla(); });

        cargarCombos();
        cargarTabla();
    }

    private int agregarCampo(JPanel form, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridx = 0; gbc.gridy = fila;
        form.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1;
        form.add(campo, gbc);
        return fila + 1;
    }

    public void refrescarDatos() {
        cargarCombos();
        cargarTabla();
    }

    private void cargarCombos() {
        try {
            comboOds.removeAllItems();
            for (Ods o : odsDAO.listarTodos()) comboOds.addItem(o);

            comboDocente.removeAllItems();
            comboDocente.addItem(null);
            for (Docente d : docenteDAO.listarTodos()) comboDocente.addItem(d);
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Curso> lista = dao.listarTodos();
            for (Curso c : lista) {
                modeloTabla.addRow(new Object[]{c.getIdCurso(), c.getNombreCurso(), c.getCapacidadMaxima(),
                        c.getCostoBase(), c.getEstadoCurso(), c.getNombreOds(),
                        c.getNombreDocente() == null ? "(sin asignar)" : c.getNombreDocente()});
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void cargarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
        txtCapacidad.setText(modeloTabla.getValueAt(fila, 2).toString());
        txtCosto.setText(modeloTabla.getValueAt(fila, 3).toString());
        comboEstado.setSelectedItem(modeloTabla.getValueAt(fila, 4));
        txtSilabo.setText("");
        seleccionarEnCombo(comboOds, (String) modeloTabla.getValueAt(fila, 5));
        String nombreDocente = (String) modeloTabla.getValueAt(fila, 6);
        if ("(sin asignar)".equals(nombreDocente)) {
            comboDocente.setSelectedItem(null);
        } else {
            seleccionarEnCombo(comboDocente, nombreDocente);
        }
    }

    private void seleccionarEnCombo(JComboBox<?> combo, String textoBuscado) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item != null && item.toString().equals(textoBuscado)) {
                combo.setSelectedItem(item);
                return;
            }
        }
    }

    private void agregar() {
        Curso c = leerFormulario();
        if (c == null) return;
        try {
            dao.insertar(c);
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
        Curso c = leerFormulario();
        if (c == null) return;
        c.setIdCurso(idSeleccionado);
        try {
            dao.actualizar(c);
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
                "¿Eliminar este curso? Esto puede fallar si ya tiene matrículas.",
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

    private Curso leerFormulario() {
        if (txtNombre.getText().isBlank() || comboOds.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "El nombre del curso y el ODS son obligatorios.");
            return null;
        }
        try {
            Curso c = new Curso();
            c.setNombreCurso(txtNombre.getText().trim());
            c.setArchivoSilabo(txtSilabo.getText().trim());
            c.setCapacidadMaxima(Integer.parseInt(txtCapacidad.getText().trim()));
            c.setCostoBase(new BigDecimal(txtCosto.getText().trim()));
            c.setEstadoCurso((String) comboEstado.getSelectedItem());
            c.setIdOds(((Ods) comboOds.getSelectedItem()).getIdOds());
            Docente docenteSel = (Docente) comboDocente.getSelectedItem();
            c.setIdDocente(docenteSel == null ? null : docenteSel.getIdDocente());
            return c;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacidad y costo deben ser numéricos.");
            return null;
        }
    }

    private void limpiar() {
        idSeleccionado = -1;
        txtNombre.setText("");
        txtSilabo.setText("");
        txtCapacidad.setText("");
        txtCosto.setText("");
        comboEstado.setSelectedIndex(0);
        if (comboOds.getItemCount() > 0) comboOds.setSelectedIndex(0);
        comboDocente.setSelectedItem(null);
        tabla.clearSelection();
    }

    private void mostrarError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error de base de datos:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
