package com.cerseu.app.vista;

import com.cerseu.app.dao.MatriculaDAO;
import com.cerseu.app.dao.PagoDAO;
import com.cerseu.app.modelo.Matricula;
import com.cerseu.app.modelo.Pago;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PanelPagos extends JPanel {

    private final PagoDAO dao = new PagoDAO();
    private final MatriculaDAO matriculaDAO = new MatriculaDAO();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Estudiante", "Curso", "Banco", "Monto (PEN)", "Fecha", "Recibo", "Verificado", "Estado"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable tabla = new JTable(modeloTabla);

    private final JComboBox<Matricula> comboMatricula = new JComboBox<>();
    private final JTextField txtBanco = new JTextField(14);
    private final JTextField txtMonto = new JTextField(8);
    private final JTextField txtFecha = new JTextField(LocalDate.now().toString(), 10);
    private final JTextField txtVoucher = new JTextField(20);
    private final JTextField txtRecibo = new JTextField(12);
    private int idSeleccionado = -1;

    public PanelPagos() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Matrícula (estudiante - curso):"), gbc);
        gbc.gridx = 1; form.add(comboMatricula, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Banco:"), gbc);
        gbc.gridx = 1; form.add(txtBanco, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Monto pagado (S/):"), gbc);
        gbc.gridx = 1; form.add(txtMonto, gbc);
        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("Fecha de pago (AAAA-MM-DD):"), gbc);
        gbc.gridx = 1; form.add(txtFecha, gbc);
        gbc.gridx = 0; gbc.gridy = 4; form.add(new JLabel("Archivo voucher:"), gbc);
        gbc.gridx = 1;
        JPanel panelVoucher = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JButton btnExaminar = new JButton("Examinar...");
        panelVoucher.add(txtVoucher);
        panelVoucher.add(btnExaminar);
        form.add(panelVoucher, gbc);
        gbc.gridx = 0; gbc.gridy = 5; form.add(new JLabel("Código de recibo:"), gbc);
        gbc.gridx = 1; form.add(txtRecibo, gbc);

        JButton btnRegistrar = new JButton("Registrar pago");
        JButton btnVerificar = new JButton("Marcar como verificado");
        JButton btnEliminar = new JButton("Eliminar pago");
        JButton btnRefrescar = new JButton("Refrescar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        botones.add(btnRegistrar);
        botones.add(btnVerificar);
        botones.add(btnEliminar);
        botones.add(btnRefrescar);

        JLabel notaMoneda = new JLabel("Nota: la moneda siempre se registra en Soles (PEN), según la regla de la tabla Pago.");
        notaMoneda.setForeground(Color.GRAY);

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        norte.add(form);
        norte.add(botones);
        norte.add(notaMoneda);

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        tabla.getSelectionModel().addListSelectionListener(ev -> {
            int fila = tabla.getSelectedRow();
            idSeleccionado = fila >= 0 ? (int) modeloTabla.getValueAt(fila, 0) : -1;
        });

        btnExaminar.addActionListener(ev -> examinarArchivo());
        btnRegistrar.addActionListener(ev -> registrar());
        btnVerificar.addActionListener(ev -> verificar());
        btnEliminar.addActionListener(ev -> eliminar());
        btnRefrescar.addActionListener(ev -> { cargarCombo(); cargarTabla(); });

        cargarCombo();
        cargarTabla();
    }

    /** Vuelve a leer el combo de matrículas y la tabla desde la base de datos. Se llama al entrar a esta pestaña. */
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
            List<Pago> lista = dao.listarTodos();
            for (Pago p : lista) {
                modeloTabla.addRow(new Object[]{p.getIdPago(), p.getNombreEstudiante(), p.getNombreCurso(),
                        p.getNombreBanco(), p.getMontoPagado(), p.getFechaPago(), p.getCodigoRecibo(),
                        p.isVerificacionVeracidad() ? "Sí" : "No", p.getEstadoPago()});
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void examinarArchivo() {
        JFileChooser chooser = new JFileChooser();
        int resultado = chooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            txtVoucher.setText(archivo.getName());
        }
    }

    private void registrar() {
        Matricula m = (Matricula) comboMatricula.getSelectedItem();
        if (m == null || txtMonto.getText().isBlank() || txtVoucher.getText().isBlank()
                || txtRecibo.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Completa matrícula, monto, voucher y código de recibo.");
            return;
        }
        try {
            Pago p = new Pago();
            p.setIdMatricula(m.getIdMatricula());
            p.setNombreBanco(txtBanco.getText().trim());
            p.setMontoPagado(new BigDecimal(txtMonto.getText().trim()));
            p.setFechaPago(LocalDate.parse(txtFecha.getText().trim()));
            p.setArchivoVoucher(txtVoucher.getText().trim());
            p.setCodigoRecibo(txtRecibo.getText().trim());
            p.setVerificacionVeracidad(false);
            p.setEstadoPago("Pendiente Verificación");
            dao.registrar(p);
            cargarTabla();
            limpiarFormulario();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El monto debe ser un número (ej. 120.00).");
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener el formato AAAA-MM-DD.");
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void verificar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un pago de la tabla primero.");
            return;
        }
        try {
            dao.verificarPago(idSeleccionado, "Completado");
            cargarTabla();
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void eliminar() {
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un pago de la tabla primero.");
            return;
        }
        try {
            dao.eliminar(idSeleccionado);
            cargarTabla();
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    private void limpiarFormulario() {
        txtBanco.setText("");
        txtMonto.setText("");
        txtVoucher.setText("");
        txtRecibo.setText("");
    }

    private void mostrarError(SQLException e) {
        JOptionPane.showMessageDialog(this, "Error de base de datos:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
