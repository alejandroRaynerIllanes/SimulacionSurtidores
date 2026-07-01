package com.simulacion.bolivia.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una estación de servicio con sus propios horarios y combustibles ofertados.
 */
public class EstacionServicio {
    private String nombre;
    private double horaApertura; // ej. 6.0
    private double horaCierre;   // ej. 22.0
    private boolean vendeGasolina;
    private boolean vendeDiesel;
    private TanqueCombustible tanqueGasolina;
    private TanqueCombustible tanqueDiesel;
    private List<Surtidor> surtidores = new ArrayList<>();

    private double distanciaKm = 0.0;
    private String conectadoA = "Internacional";

    public EstacionServicio() {
    }

    public EstacionServicio(String nombre, double horaApertura, double horaCierre, boolean vendeGasolina, boolean vendeDiesel, TanqueCombustible tanqueGasolina, TanqueCombustible tanqueDiesel) {
        this.nombre = nombre;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.vendeGasolina = vendeGasolina;
        this.vendeDiesel = vendeDiesel;
        this.tanqueGasolina = tanqueGasolina;
        this.tanqueDiesel = tanqueDiesel;
    }

    public boolean estaAbierta(double relojSimulacionMinutos) {
        double horaActual = (relojSimulacionMinutos / 60.0) % 24.0;
        return horaActual >= horaApertura && horaActual < horaCierre;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(double horaApertura) {
        this.horaApertura = horaApertura;
    }

    public double getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(double horaCierre) {
        this.horaCierre = horaCierre;
    }

    public boolean isVendeGasolina() {
        return vendeGasolina;
    }

    public void setVendeGasolina(boolean vendeGasolina) {
        this.vendeGasolina = vendeGasolina;
    }

    public boolean isVendeDiesel() {
        return vendeDiesel;
    }

    public void setVendeDiesel(boolean vendeDiesel) {
        this.vendeDiesel = vendeDiesel;
    }

    public TanqueCombustible getTanqueGasolina() {
        return tanqueGasolina;
    }

    public void setTanqueGasolina(TanqueCombustible tanqueGasolina) {
        this.tanqueGasolina = tanqueGasolina;
    }

    public TanqueCombustible getTanqueDiesel() {
        return tanqueDiesel;
    }

    public void setTanqueDiesel(TanqueCombustible tanqueDiesel) {
        this.tanqueDiesel = tanqueDiesel;
    }

    public List<Surtidor> getSurtidores() {
        return surtidores;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public void setSurtidores(List<Surtidor> surtidores) {
        this.surtidores = surtidores;
    }

    public String getConectadoA() {
        return conectadoA;
    }

    public void setConectadoA(String conectadoA) {
        this.conectadoA = conectadoA;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
