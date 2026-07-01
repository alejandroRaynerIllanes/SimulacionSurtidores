package com.simulacion.bolivia.models;

/**
 * Representa el tanque de almacenamiento de combustible en la estación de servicio.
 */
public class TanqueCombustible {
    private String tipoCombustible; // "Gasolina Especial" o "Diésel"
    private double capacidadMaxima = 30000.0; // Inicializado en 30000.0
    private double nivelActual = 30000.0; // Inicializado en 30000.0

    /**
     * Constructor vacío por defecto.
     */
    public TanqueCombustible() {
    }

    /**
     * Constructor completo que permite inicializar todos los campos manualmente.
     * 
     * @param tipoCombustible el tipo de combustible ("Gasolina Especial" o "Diésel").
     * @param capacidadMaxima capacidad máxima del tanque.
     * @param nivelActual nivel actual del combustible en el tanque.
     */
    public TanqueCombustible(String tipoCombustible, double capacidadMaxima, double nivelActual) {
        this.tipoCombustible = tipoCombustible;
        this.capacidadMaxima = capacidadMaxima;
        this.nivelActual = nivelActual;
    }

    /**
     * Constructor alternativo que inicializa el tanque con capacidad y nivel máximo por defecto (30,000.0 L).
     * 
     * @param tipoCombustible el tipo de combustible ("Gasolina Especial" o "Diésel").
     */
    public TanqueCombustible(String tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
        this.capacidadMaxima = 30000.0;
        this.nivelActual = 30000.0;
    }

    /**
     * Verifica si existe suficiente stock en el tanque para la cantidad pedida.
     * 
     * @param cantidadPedida litros de combustible requeridos.
     * @return true si el nivel actual es mayor o igual a la cantidad pedida, de lo contrario false.
     */
    public boolean hayStock(double cantidadPedida) {
        return this.nivelActual >= cantidadPedida;
    }

    /**
     * Descuenta la cantidad especificada de combustible del stock actual del tanque.
     * Si la cantidad excede el stock, el nivel se establece a 0.
     * 
     * @param cantidad litros de combustible a descontar.
     */
    public void descontarStock(double cantidad) {
        if (hayStock(cantidad)) {
            this.nivelActual -= cantidad;
        } else {
            this.nivelActual = 0.0;
        }
    }

    // Getters y Setters

    public String getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(String tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public double getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(double capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public double getNivelActual() {
        return nivelActual;
    }

    public void setNivelActual(double nivelActual) {
        if (nivelActual > this.capacidadMaxima) {
            this.nivelActual = this.capacidadMaxima;
        } else if (nivelActual < 0) {
            this.nivelActual = 0.0;
        } else {
            this.nivelActual = nivelActual;
        }
    }

    @Override
    public String toString() {
        return "TanqueCombustible{" +
                "tipoCombustible='" + tipoCombustible + '\'' +
                ", capacidadMaxima=" + capacidadMaxima +
                ", nivelActual=" + nivelActual +
                '}';
    }
}
