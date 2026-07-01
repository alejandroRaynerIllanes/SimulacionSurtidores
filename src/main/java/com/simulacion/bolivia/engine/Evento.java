package com.simulacion.bolivia.engine;

import com.simulacion.bolivia.models.Surtidor;
import com.simulacion.bolivia.models.Vehiculo;

/**
 * Representa un evento en la simulación de eventos discretos.
 * Cada evento tiene asociado un tiempo de ejecución y se ordena cronológicamente.
 */
public class Evento implements Comparable<Evento> {
    private final double tiempo;
    private final TipoEvento tipo;
    private final Vehiculo vehiculo; // Puede ser null
    private final Surtidor surtidor; // Puede ser null

    /**
     * Constructor completo del evento.
     * 
     * @param tiempo instante en el que ocurre el evento (en minutos).
     * @param tipo tipo de evento.
     * @param vehiculo vehículo asociado al evento (si aplica).
     * @param surtidor surtidor asociado al evento (si aplica).
     */
    public Evento(double tiempo, TipoEvento tipo, Vehiculo vehiculo, Surtidor surtidor) {
        this.tiempo = tiempo;
        this.tipo = tipo;
        this.vehiculo = vehiculo;
        this.surtidor = surtidor;
    }

    public double getTiempo() {
        return tiempo;
    }

    public TipoEvento getTipo() {
        return tipo;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Surtidor getSurtidor() {
        return surtidor;
    }

    @Override
    public int compareTo(Evento o) {
        return Double.compare(this.tiempo, o.tiempo);
    }

    @Override
    public String toString() {
        return "Evento{" +
                "tiempo=" + tiempo +
                ", tipo=" + tipo +
                ", vehiculo=" + (vehiculo != null ? vehiculo.getId() : "null") +
                ", surtidor=" + (surtidor != null ? surtidor.getId() : "null") +
                '}';
    }
}
