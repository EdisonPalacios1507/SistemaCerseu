package com.cerseu.app.modelo;

public class Docente {
    private int idDocente;
    private String nombreCompleto;

    public Docente() {}

    public Docente(int idDocente, String nombreCompleto) {
        this.idDocente = idDocente;
        this.nombreCompleto = nombreCompleto;
    }

    public int getIdDocente() { return idDocente; }
    public void setIdDocente(int idDocente) { this.idDocente = idDocente; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    @Override
    public String toString() {
        return nombreCompleto;
    }
}
