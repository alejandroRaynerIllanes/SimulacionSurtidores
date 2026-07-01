package com.simulacion.bolivia.reports;

/**
 * Clase DTO (Data Transfer Object) que almacena los datos históricos
 * de cada vehículo atendido en el sistema.
 */
public class RegistroOperacion {
    private final double tiempoSalida;
    private final String tipoVehiculo;
    private final String nombreEstacion;
    private final String idSurtidor;
    private final double tiempoEsperaMin;
    private final double litrosCargados;
    private final double montoPagadoBs;

    public RegistroOperacion(double tiempoSalida, String tipoVehiculo, String nombreEstacion,
                             String idSurtidor, double tiempoEsperaMin, double litrosCargados,
                             double montoPagadoBs) {
        this.tiempoSalida = tiempoSalida;
        this.tipoVehiculo = tipoVehiculo;
        this.nombreEstacion = nombreEstacion;
        this.idSurtidor = idSurtidor;
        this.tiempoEsperaMin = tiempoEsperaMin;
        this.litrosCargados = litrosCargados;
        this.montoPagadoBs = montoPagadoBs;
    }

    public double getTiempoSalida() {
        return tiempoSalida;
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }

    public String getNombreEstacion() {
        return nombreEstacion;
    }

    public String getIdSurtidor() {
        return idSurtidor;
    }

    public double getTiempoEsperaMin() {
        return tiempoEsperaMin;
    }

    public double getLitrosCargados() {
        return litrosCargados;
    }

    public double getMontoPagadoBs() {
        return montoPagadoBs;
    }
}
