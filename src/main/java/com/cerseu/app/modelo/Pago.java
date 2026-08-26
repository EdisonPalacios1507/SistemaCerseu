package com.cerseu.app.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Pago {
    private int idPago;
    private int idMatricula;
    private String nombreEstudiante; // para JOIN
    private String nombreCurso;      // para JOIN
    private String nombreBanco;
    private BigDecimal montoPagado;
    private LocalDate fechaPago;
    private String archivoVoucher;
    private String codigoRecibo;
    private boolean verificacionVeracidad;
    private String codigoMoneda; // siempre 'PEN'
    private String estadoPago;

    public Pago() {}

    public int getIdPago() { return idPago; }
    public void setIdPago(int idPago) { this.idPago = idPago; }

    public int getIdMatricula() { return idMatricula; }
    public void setIdMatricula(int idMatricula) { this.idMatricula = idMatricula; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }

    public String getNombreCurso() { return nombreCurso; }
    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }

    public String getNombreBanco() { return nombreBanco; }
    public void setNombreBanco(String nombreBanco) { this.nombreBanco = nombreBanco; }

    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public String getArchivoVoucher() { return archivoVoucher; }
    public void setArchivoVoucher(String archivoVoucher) { this.archivoVoucher = archivoVoucher; }

    public String getCodigoRecibo() { return codigoRecibo; }
    public void setCodigoRecibo(String codigoRecibo) { this.codigoRecibo = codigoRecibo; }

    public boolean isVerificacionVeracidad() { return verificacionVeracidad; }
    public void setVerificacionVeracidad(boolean verificacionVeracidad) { this.verificacionVeracidad = verificacionVeracidad; }

    public String getCodigoMoneda() { return codigoMoneda; }
    public void setCodigoMoneda(String codigoMoneda) { this.codigoMoneda = codigoMoneda; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
}
