package com.cerseu.app.modelo;

public class Estudiante {
    private int idEstudiante;
    private String nombreCompleto;
    private String documentoIdentidad;
    private String correoElectronico;
    private String telefono;

    public Estudiante() {}

    public Estudiante(int idEstudiante, String nombreCompleto, String documentoIdentidad,
                       String correoElectronico, String telefono) {
        this.idEstudiante = idEstudiante;
        this.nombreCompleto = nombreCompleto;
        this.documentoIdentidad = documentoIdentidad;
        this.correoElectronico = correoElectronico;
        this.telefono = telefono;
    }

    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int idEstudiante) { this.idEstudiante = idEstudiante; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public void setDocumentoIdentidad(String documentoIdentidad) { this.documentoIdentidad = documentoIdentidad; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return nombreCompleto + " (" + documentoIdentidad + ")";
    }
}
