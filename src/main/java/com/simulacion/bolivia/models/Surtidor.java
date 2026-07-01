package com.simulacion.bolivia.models;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Representa un surtidor (bomba de despacho) en la estación de servicio.
 */
public class Surtidor {
    private String id; // Ej: "S1", "S_Int"
    private String tipo; // "Subvencionado" o "Internacional"
    private double precioPorLitro;
    private boolean estaOcupado = false; // Inicializar en false
    private TanqueCombustible tanqueAsignado; // Referencia al tanque del que extrae
    private double caudalLitrosPorMinuto = 60.0; // Inicializar en 60.0 para modelar tiempo de bombeo físico
    private Queue<Vehiculo> filaEspera = new LinkedList<>();

    /**
     * Constructor vacío por defecto.
     */
    public Surtidor() {
    }

    /**
     * Constructor completo que permite inicializar todos los campos manualmente.
     * 
     * @param id identificador del surtidor.
     * @param tipo tipo de surtidor ("Subvencionado" o "Internacional").
     * @param precioPorLitro precio unitario del combustible por litro en este surtidor.
     * @param estaOcupado estado actual del surtidor (ocupado o disponible).
     * @param tanqueAsignado referencia al tanque del que extrae el combustible.
     * @param caudalLitrosPorMinuto caudal en litros por minuto.
     */
    public Surtidor(String id, String tipo, double precioPorLitro, boolean estaOcupado, TanqueCombustible tanqueAsignado, double caudalLitrosPorMinuto) {
        this.id = id;
        this.tipo = tipo;
        this.precioPorLitro = precioPorLitro;
        this.estaOcupado = estaOcupado;
        this.tanqueAsignado = tanqueAsignado;
        this.caudalLitrosPorMinuto = caudalLitrosPorMinuto;
    }

    /**
     * Constructor alternativo con valores por defecto para caudal (60.0 L/min) y estado ocupado (false).
     * 
     * @param id identificador del surtidor.
     * @param tipo tipo de surtidor ("Subvencionado" o "Internacional").
     * @param precioPorLitro precio unitario del combustible por litro.
     * @param tanqueAsignado referencia al tanque de combustible.
     */
    public Surtidor(String id, String tipo, double precioPorLitro, TanqueCombustible tanqueAsignado) {
        this.id = id;
        this.tipo = tipo;
        this.precioPorLitro = precioPorLitro;
        this.tanqueAsignado = tanqueAsignado;
        this.estaOcupado = false;
        this.caudalLitrosPorMinuto = 60.0;
    }

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getPrecioPorLitro() {
        return precioPorLitro;
    }

    public void setPrecioPorLitro(double precioPorLitro) {
        this.precioPorLitro = precioPorLitro;
    }

    public boolean isEstaOcupado() {
        return estaOcupado;
    }

    public void setEstaOcupado(boolean estaOcupado) {
        this.estaOcupado = estaOcupado;
    }

    public TanqueCombustible getTanqueAsignado() {
        return tanqueAsignado;
    }

    public void setTanqueAsignado(TanqueCombustible tanqueAsignado) {
        this.tanqueAsignado = tanqueAsignado;
    }

    public double getCaudalLitrosPorMinuto() {
        return caudalLitrosPorMinuto;
    }

    public void setCaudalLitrosPorMinuto(double caudalLitrosPorMinuto) {
        this.caudalLitrosPorMinuto = caudalLitrosPorMinuto;
    }

    public Queue<Vehiculo> getFilaEspera() {
        return filaEspera;
    }

    public int getTamanoFila() {
        return filaEspera.size();
    }

    @Override
    public String toString() {
        return id + " (" + tipo + ") - " + (int)caudalLitrosPorMinuto + " L/min - Precio: " + precioPorLitro + " Bs";
    }
}
