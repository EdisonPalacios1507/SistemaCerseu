package com.cerseu.app.vista;

import com.cerseu.app.dao.CursoDAO;
import com.cerseu.app.dao.EstudianteDAO;
import com.cerseu.app.dao.MatriculaDAO;
import com.cerseu.app.modelo.Curso;
import com.cerseu.app.modelo.Estudiante;
import com.cerseu.app.modelo.Matricula;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelMatricula extends JPanel {

    private final MatriculaDAO dao = new MatriculaDAO();
    private final EstudianteDAO estudianteDAO = new EstudianteDAO();
    private final CursoDAO cursoDAO = new CursoDAO();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID Matrícula", "Estudiante", "Curso", "Periodo", "Estado"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    private final JComboBox<Estudiante> comboEstudiante = new JComboBox<>();
    private final JComboBox<Curso> comboCurso = new JComboBox<>();
    private final JTextField txtPeriodo = new JTextField("2025-0", 8);
    private final JComboBox<String> comboEstado = new JComboBox<>(
            new String[]{"Pendiente Pago", "Inscrito", "Retirado"});
    private int idSeleccionado = -1;

    public PanelMatricula() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Estudiante:"), gbc);
        gbc.gridx = 1; form.add(comboEstudiante, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Curso:"), gbc);
        gbc.gridx = 1; form.add(comboCurso, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Periodo académico:"), gbc);
        gbc.gridx = 1; form.add(txtPeriodo, gbc);

        JButton btnMatricular = new JButton("Matricular (usa sp_matricular_estudiante)");
        JButton btnRefrescar = new JButton("Refrescar listas");

        JPanel accionesMatricula = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        accionesMatricula.add(btnMatricular);
        accionesMatricula.add(btnRefrescar);

        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panelEstado.add(new JLabel("Cambiar estado de matrícula seleccionada a:"));
        panelEstado.add(comboEstado);
        JButton btnCambiarEstado = new JButton("Aplicar estado");
        JButton btnEliminar = new JButton("Eliminar matrícula");
        panelEstado.add(btnCambiarEstado);
        panelEstado.add(btnEliminar);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(form);
        norte.add(accionesMatricula);
        norte.add(panelEstado);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(ev -> {
            int fila = tabla.getSelectedRow();
            idSeleccionado = fila >= 0 ? (int) modeloTabla.getValueAt(fila, 0) : -1;
        });

        btnMatricular.addActionListener(ev -> matricular());
        btnRefrescar.addActionListener(ev -> cargarCombos());
        btnCambiarEstado.addActionListener(ev -> cambiarEstado());
        btnEliminar.addActionListener(ev -> eliminar());

        cargarCombos();
        cargarTabla();
    }

    /** Vuelve a leer combos y tabla desde la base de datos. Se llama al entrar a esta pestaña. */
    public void refrescarDatos() {
        cargarCombos();
        cargarTabla();
    }

    private void cargarCombos() {
        try {
            comboEstudiante.removeAllItems();
            for (Estudiante e : estudianteDAO.listarTodos()) comboEstudiante.addItem(e);

            comboCurso.removeAllItems();
            for (Curso c : cursoDAO.listarTodos()) comboCurso.addItem(c);
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Matricula> lista = dao.listarTodas();
            for (Matricula m : lista) {
                modeloTabla.addRow(new Object[]{m.getIdMatricula(), m.getNombreEstudiante(),
                        m.getNombreCurso(), m.getPeriodoAcademico(), m.getEstadoMatricula()});
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /**
     * Llama al procedimiento almacenado sp_matricular_estudiante. Toda la
     * validación de vacantes y estado del curso (Regla de negocio 4) ocurre
     * dentro de MySQL; aquí solo se muestra el resultado o el error tal cual
     * lo señala el SIGNAL SQLSTATE '45000' del procedimiento.
     */
    private void matricular() {
        Estudiante e = (Estudiante) comboEstudiante.getSelectedItem();
        Curso c = (Curso) comboCurso.getSelectedItem();
        String periodo = txtPeriodo.getText().trim();

        if (e == null || c == null || periodo.isBlank()) {
            JOptionPane.showMessageDialog(this, "Selecciona estudiante, curso e indica el periodo.");
            return;
        }
        try {
            dao.matricular(e.getIdEstudiante(), c.getIdCurso(), periodo);
            JOptionPane.showMessageDialog(this, "Matrícula registrada correctamente.");
            cargarTabla();
        } catch (SQLException ex) {
            // Aquí llegan los mensajes definidos con SIGNAL en el procedimiento
            // (curso no habilitado, capacidad máxima alcanzada, matrícula duplicada, etc.)
            JOptionPane.showMessageDialog(this, "No se pudo matricular:\n" + ex.getMessage(),
                    "Regla de negocio", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cambiarEstado() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una matrícula de la tabla primero.");
            return;
        }
        try {
            dao.actualizarEstado(idSeleccionado, (String) comboEstado.getSelectedItem());
            cargarTabla();
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una matrícula de la tabla primero.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Eliminar esta matrícula? Esto puede fallar si ya tiene asistencias, notas o pagos asociados.",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;
        try {
            dao.eliminar(idSeleccionado);
            cargarTabla();
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void mostrarError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error de base de datos:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
