package com.cerseu.app.vista;

import com.cerseu.app.dao.ActaNotasDAO;
import com.cerseu.app.dao.AsistenciaDAO;
import com.cerseu.app.dao.MatriculaDAO;
import com.cerseu.app.modelo.ActaNotas;
import com.cerseu.app.modelo.Matricula;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

public class PanelActaNotas extends JPanel {

    private final ActaNotasDAO dao = new ActaNotasDAO();
    private final MatriculaDAO matriculaDAO = new MatriculaDAO();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID Acta", "Estudiante", "Curso", "Nota (0-20)", "% Asistencia", "Estado final", "¿Cerrada?"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    private final JComboBox<Matricula> comboMatricula = new JComboBox<>();
    private final JSpinner spinnerNota = new JSpinner(new SpinnerNumberModel(11, 0, 20, 1));
    private final JTextField txtPorcentaje = new JTextField(6);
    private int idActaSeleccionada = -1;

    public PanelActaNotas() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Matrícula (estudiante - curso):"), gbc);
        gbc.gridx = 1; form.add(comboMatricula, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Nota final (escala vigesimal):"), gbc);
        gbc.gridx = 1; form.add(spinnerNota, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("% de asistencia:"), gbc);
        gbc.gridx = 1;
        JPanel panelPorcentaje = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JButton btnCalcular = new JButton("Calcular desde asistencias");
        panelPorcentaje.add(txtPorcentaje);
        panelPorcentaje.add(btnCalcular);
        form.add(panelPorcentaje, gbc);

        JButton btnRegistrar = new JButton("Registrar acta (dispara el trigger de cálculo)");
        JButton btnCerrar = new JButton("Cerrar acta seleccionada");
        JButton btnRefrescar = new JButton("Refrescar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.add(btnRegistrar);
        botones.add(btnCerrar);
        botones.add(btnRefrescar);

        JLabel nota = new JLabel("<html><i>Nota: el \"Estado final\" lo calcula automáticamente MySQL "
                + "(trigger trg_calcular_estado_final). Un acta cerrada no admite cambios "
                + "(trigger trg_bloquear_acta_cerrada, Regla 13).</i></html>");
        nota.setForeground(Color.GRAY);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(form);
        norte.add(botones);
        norte.add(nota);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(ev -> {
            int fila = tabla.getSelectedRow();
            idActaSeleccionada = fila >= 0 ? (int) modeloTabla.getValueAt(fila, 0) : -1;
        });

        btnCalcular.addActionListener(ev -> calcularPorcentaje());
        btnRegistrar.addActionListener(ev -> registrar());
        btnCerrar.addActionListener(ev -> cerrarActa());
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
            for (Matricula m : matriculas) comboMatricula.addItem(m);
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
            List<ActaNotas> lista = dao.listarTodas();
            for (ActaNotas a : lista) {
                modeloTabla.addRow(new Object[]{a.getIdActa(), a.getNombreEstudiante(), a.getNombreCurso(),
                        a.getNotaFinal(), a.getPorcentajeAsistencia(), a.getEstadoFinal(),
                        a.isEstaCerrada() ? "Sí" : "No"});
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void calcularPorcentaje() {
        Matricula m = (Matricula) comboMatricula.getSelectedItem();
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Selecciona primero una matrícula.");
            return;
        }
        try {
            double porcentaje = asistenciaDAO.calcularPorcentajeAsistencia(m.getIdMatricula());
            txtPorcentaje.setText(BigDecimal.valueOf(porcentaje).setScale(2, RoundingMode.HALF_UP).toString());
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void registrar() {
        Matricula m = (Matricula) comboMatricula.getSelectedItem();
        if (m == null || txtPorcentaje.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Selecciona la matrícula e ingresa (o calcula) el % de asistencia.");
            return;
        }
        try {
            ActaNotas acta = new ActaNotas();
            acta.setIdMatricula(m.getIdMatricula());
            acta.setNotaFinal((Integer) spinnerNota.getValue());
            acta.setPorcentajeAsistencia(new BigDecimal(txtPorcentaje.getText().trim()));
            dao.insertar(acta);
            cargarTabla();
            JOptionPane.showMessageDialog(this,
                    "Acta registrada. El estado final fue calculado automáticamente por la base de datos.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El % de asistencia debe ser un número (ej. 85.00).");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void cerrarActa() {
        if (idActaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un acta de la tabla primero.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Cerrar esta acta? Una vez cerrada, no podrá modificarse (Regla 13).",
                "Confirmar cierre", JOptionPane.YES_NO_OPTION);
        if (confirmar != JOptionPane.YES_OPTION) return;
        try {
            dao.cerrarActa(idActaSeleccionada);
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
