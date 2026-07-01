package com.simulacion.bolivia.models;

/**
 * Representa un vehículo que arriba a la estación de servicio para abastecerse.
 */
public class Vehiculo {
    private String id;
    private String perfil; // "Particular" o "Transporte Pesado"
    private double volumenRequerido; // En litros
    private double costoOportunidadHora; // 30.0 para Particular, 150.0 para Pesado
    private double horaArriboSimulacion; // Minuto exacto en el que llega al sistema

    /**
     * Constructor vacío por defecto.
     */
    public Vehiculo() {
    }

    /**
     * Constructor completo que permite inicializar todos los campos manualmente.
     * 
     * @param id identificador del vehículo.
     * @param perfil tipo de perfil ("Particular" o "Transporte Pesado").
     * @param volumenRequerido cantidad de combustible requerida en litros.
     * @param costoOportunidadHora costo de oportunidad por hora de espera.
     * @param horaArriboSimulacion minuto de arribo en la simulación.
     */
    public Vehiculo(String id, String perfil, double volumenRequerido, double costoOportunidadHora, double horaArriboSimulacion) {
        this.id = id;
        this.perfil = perfil;
        this.volumenRequerido = volumenRequerido;
        this.costoOportunidadHora = costoOportunidadHora;
        this.horaArriboSimulacion = horaArriboSimulacion;
    }

    /**
     * Constructor alternativo que calcula automáticamente el costo de oportunidad basado en el perfil del vehículo.
     * 
     * @param id identificador del vehículo.
     * @param perfil tipo de perfil ("Particular" o "Transporte Pesado").
     * @param volumenRequerido cantidad de combustible requerida en litros.
     * @param horaArriboSimulacion minuto de arribo en la simulación.
     */
    public Vehiculo(String id, String perfil, double volumenRequerido, double horaArriboSimulacion) {
        this.id = id;
        this.perfil = perfil;
        this.volumenRequerido = volumenRequerido;
        this.horaArriboSimulacion = horaArriboSimulacion;
        this.costoOportunidadHora = calcularCostoOportunidadPorDefecto(perfil);
    }

    /**
     * Método auxiliar para calcular el costo de oportunidad según el perfil del vehículo.
     */
    private double calcularCostoOportunidadPorDefecto(String perfil) {
        if ("Particular".equalsIgnoreCase(perfil)) {
            return 30.0;
        } else if ("Transporte Pesado".equalsIgnoreCase(perfil)) {
            return 150.0;
        }
        return 0.0;
    }

    // Getters y Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
        this.costoOportunidadHora = calcularCostoOportunidadPorDefecto(perfil);
    }

    public double getVolumenRequerido() {
        return volumenRequerido;
    }

    public void setVolumenRequerido(double volumenRequerido) {
        this.volumenRequerido = volumenRequerido;
    }

    public double getCostoOportunidadHora() {
        return costoOportunidadHora;
    }

    public void setCostoOportunidadHora(double costoOportunidadHora) {
        this.costoOportunidadHora = costoOportunidadHora;
    }

    public double getHoraArriboSimulacion() {
        return horaArriboSimulacion;
    }

    public void setHoraArriboSimulacion(double horaArriboSimulacion) {
        this.horaArriboSimulacion = horaArriboSimulacion;
    }

    @Override
    public String toString() {
        return "Vehiculo{" +
                "id='" + id + '\'' +
                ", perfil='" + perfil + '\'' +
                ", volumenRequerido=" + volumenRequerido +
                ", costoOportunidadHora=" + costoOportunidadHora +
                ", horaArriboSimulacion=" + horaArriboSimulacion +
                '}';
    }
}
