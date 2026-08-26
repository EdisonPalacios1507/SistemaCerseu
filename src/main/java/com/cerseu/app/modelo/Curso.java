package com.cerseu.app.modelo;

import java.math.BigDecimal;

public class Curso {
    private int idCurso;
    private String nombreCurso;
    private String archivoSilabo;
    private int capacidadMaxima;
    private BigDecimal costoBase;
    private String estadoCurso; 
    private int idOds;
    private String nombreOds; 
    private Integer idDocente;
    private String nombreDocente; 

    public Curso() {}

    public int getIdCurso() { return idCurso; }
    public void setIdCurso(int idCurso) { this.idCurso = idCurso; }

    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }

    public String getArchivoSilabo() { return archivoSilabo; }
    public void setArchivoSilabo(String archivoSilabo) { this.archivoSilabo = archivoSilabo; }

    public int getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(int capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

    public BigDecimal getCostoBase() { return costoBase; }
    public void setCostoBase(BigDecimal costoBase) { this.costoBase = costoBase; }

    public String getEstadoCurso() { return estadoCurso; }
    public void setEstadoCurso(String estadoCurso) { this.estadoCurso = estadoCurso; }

    public int getIdOds() { return idOds; }
    public void setIdOds(int idOds) { this.idOds = idOds; }

    public String getNombreOds() { return nombreOds; }
    public void setNombreOds(String nombreOds) { this.nombreOds = nombreOds; }

    public Integer getIdDocente() { return idDocente; }
    public void setIdDocente(Integer idDocente) { this.idDocente = idDocente; }

    public String getNombreDocente() { return nombreDocente; }
    public void setNombreDocente(String nombreDocente) { this.nombreDocente = nombreDocente; }

    @Override
    public String toString() {
        return nombreCurso;
    }
}
