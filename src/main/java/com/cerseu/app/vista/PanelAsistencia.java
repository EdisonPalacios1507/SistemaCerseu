package com.cerseu.app.vista;

import com.cerseu.app.dao.AsistenciaDAO;
import com.cerseu.app.dao.MatriculaDAO;
import com.cerseu.app.modelo.Asistencia;
import com.cerseu.app.modelo.Matricula;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PanelAsistencia extends JPanel {

    private final AsistenciaDAO dao = new AsistenciaDAO();
    private final MatriculaDAO matriculaDAO = new MatriculaDAO();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Estudiante", "Curso", "Fecha", "Estado"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    private final JComboBox<Matricula> comboMatricula = new JComboBox<>();
    private final JTextField txtFecha = new JTextField(LocalDate.now().toString(), 10);
    private final JComboBox<String> comboEstado = new JComboBox<>(new String[]{"Presente", "Ausente", "Tardanza"});
    private int idSeleccionado = -1;

    public PanelAsistencia() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Matrícula (estudiante - curso):"), gbc);
        gbc.gridx = 1; form.add(comboMatricula, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Fecha de sesión (AAAA-MM-DD):"), gbc);
        gbc.gridx = 1; form.add(txtFecha, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Estado de asistencia:"), gbc);
        gbc.gridx = 1; form.add(comboEstado, gbc);

        JButton btnRegistrar = new JButton("Registrar asistencia");
        JButton btnEliminar = new JButton("Eliminar seleccionada");
        JButton btnRefrescar = new JButton("Refrescar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.add(btnRegistrar);
        botones.add(btnEliminar);
        botones.add(btnRefrescar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(form, BorderLayout.NORTH);
        norte.add(botones, BorderLayout.SOUTH);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(ev -> {
            int fila = tabla.getSelectedRow();
            idSeleccionado = fila >= 0 ? (int) modeloTabla.getValueAt(fila, 0) : -1;
        });

        btnRegistrar.addActionListener(ev -> registrar());
        btnEliminar.addActionListener(ev -> eliminar());
        btnRefrescar.addActionListener(ev -> { cargarCombo(); cargarTabla(); });

        cargarCombo();
        cargarTabla();
    }

    public void refrescarDatos() {
        cargarCombo();
        cargarTabla();
    }

    private void cargarCombo() {
        try {
            comboMatricula.removeAllItems();
            List<Matricula> matriculas = matriculaDAO.listarTodas();
            for (Matricula m : matriculas) {
                comboMatricula.addItem(m);
            }
            comboMatricula.setRenderer((lista, valor, indice, seleccionado, foco) -> {
                JLabel label = new JLabel();
                if (valor != null) {
                    Matricula m = (Matricula) valor;
                    label.setText(m.getNombreEstudiante() + " — " + m.getNombreCurso()
                            + " (" + m.getPeriodoAcademico() + ")");
                }
                return label;
            });
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void cargarTabla() {
        try {
            modeloTabla.setRowCount(0);
            List<Asistencia> lista = dao.listarTodas();
            for (Asistencia a : lista) {
                modeloTabla.addRow(new Object[]{a.getIdAsistencia(), a.getNombreEstudiante(),
                        a.getNombreCurso(), a.getFechaSesion(), a.getEstadoAsistencia()});
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void registrar() {
        Matricula m = (Matricula) comboMatricula.getSelectedItem();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una matrícula.");
            return;
        }
        try {
            Asistencia a = new Asistencia();
            a.setIdMatricula(m.getIdMatricula());
            a.setFechaSesion(LocalDate.parse(txtFecha.getText().trim()));
            a.setEstadoAsistencia((String) comboEstado.getSelectedItem());
            dao.registrar(a);
            cargarTabla();
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener el formato AAAA-MM-DD.");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila de la tabla primero.");
            return;
        }
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
