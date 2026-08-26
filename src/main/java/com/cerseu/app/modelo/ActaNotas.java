package com.cerseu.app.modelo;

import java.math.BigDecimal;

public class ActaNotas {
    private int idActa;
    private int idMatricula;
    private String nombreEstudiante; 
    private String nombreCurso; 
    private Integer notaFinal; 
    private BigDecimal porcentajeAsistencia;
    private String estadoFinal;
    private boolean estaCerrada;

    public ActaNotas() {}

    public int getIdActa() { return idActa; }
    public void setIdActa(int idActa) { this.idActa = idActa; }

    public int getIdMatricula() { return idMatricula; }
    public void setIdMatricula(int idMatricula) { this.idMatricula = idMatricula; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }

    public Integer getNotaFinal() { return notaFinal; }
    public void setNotaFinal(Integer notaFinal) { this.notaFinal = notaFinal; }

    public BigDecimal getPorcentajeAsistencia() { return porcentajeAsistencia; }
    public void setPorcentajeAsistencia(BigDecimal porcentajeAsistencia) { this.porcentajeAsistencia = porcentajeAsistencia; }

    public String getEstadoFinal() { return estadoFinal; }
    public void setEstadoFinal(String estadoFinal) { this.estadoFinal = estadoFinal; }

    public boolean isEstaCerrada() { return estaCerrada; }
    public void setEstaCerrada(boolean estaCerrada) { this.estaCerrada = estaCerrada; }
}
