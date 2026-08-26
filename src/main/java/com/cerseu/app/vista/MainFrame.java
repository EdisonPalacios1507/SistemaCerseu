package com.cerseu.app.vista;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final PanelEstudiantes panelEstudiantes = new PanelEstudiantes();
    private final PanelDocentes panelDocentes = new PanelDocentes();
    private final PanelODS panelODS = new PanelODS();
    private final PanelCursos panelCursos = new PanelCursos();
    private final PanelMatricula panelMatricula = new PanelMatricula();
    private final PanelAsistencia panelAsistencia = new PanelAsistencia();
    private final PanelActaNotas panelActaNotas = new PanelActaNotas();
    private final PanelPagos panelPagos = new PanelPagos();

    public MainFrame() {
        super("Sistema de Gestión Académica - CERSEU Arguedas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 700);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(construirEncabezado(), BorderLayout.NORTH);
        add(construirPestanas(), BorderLayout.CENTER);
    }

    private JPanel construirEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(14, 20, 10, 20));

        JLabel titulo = new JLabel("CERSEU · Centro de Información - Sistema de Cursos y Matrícula");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));

        JLabel subtitulo = new JLabel("Base de datos: arguedas_cerseu");
        subtitulo.setFont(subtitulo.getFont().deriveFont(Font.PLAIN, 12f));
        subtitulo.setForeground(Color.GRAY);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(titulo);
        textos.add(subtitulo);

        panel.add(textos, BorderLayout.WEST);
        return panel;
    }

    private JTabbedPane construirPestanas() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));

        tabs.addTab("Estudiantes", panelEstudiantes);
        tabs.addTab("Docentes", panelDocentes);
        tabs.addTab("ODS", panelODS);
        tabs.addTab("Cursos", panelCursos);
        tabs.addTab("Matrícula", panelMatricula);
        tabs.addTab("Asistencia", panelAsistencia);
        tabs.addTab("Actas de Notas", panelActaNotas);
        tabs.addTab("Pagos", panelPagos);

        tabs.addChangeListener(ev -> {
            Component seleccionado = tabs.getSelectedComponent();
            if (seleccionado == panelEstudiantes) panelEstudiantes.refrescarDatos();
            else if (seleccionado == panelDocentes) panelDocentes.refrescarDatos();
            else if (seleccionado == panelODS) panelODS.refrescarDatos();
            else if (seleccionado == panelCursos) panelCursos.refrescarDatos();
            else if (seleccionado == panelMatricula) panelMatricula.refrescarDatos();
            else if (seleccionado == panelAsistencia) panelAsistencia.refrescarDatos();
            else if (seleccionado == panelActaNotas) panelActaNotas.refrescarDatos();
            else if (seleccionado == panelPagos) panelPagos.refrescarDatos();
        });

        return tabs;
    }
}
