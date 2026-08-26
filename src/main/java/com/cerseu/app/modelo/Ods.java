package com.cerseu.app.modelo;

public class Ods {
    private int idOds;
    private String nombreCategoria;

    public Ods() {}

    public Ods(int idOds, String nombreCategoria) {
        this.idOds = idOds;
        this.nombreCategoria = nombreCategoria;
    }

    public int getIdOds() { return idOds; }
    public void setIdOds(int idOds) { this.idOds = idOds; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    @Override
    public String toString() {
        return nombreCategoria;
    }
}
