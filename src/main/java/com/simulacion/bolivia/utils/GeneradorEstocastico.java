package com.simulacion.bolivia.utils;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Utilidad para la generación de variables aleatorias basadas en distribuciones de probabilidad.
 * Utiliza la biblioteca Apache Commons Math 3.
 */
public class GeneradorEstocastico {

    // Instancias estáticas de las distribuciones para evitar recreación de objetos
    private static final NormalDistribution distVolParticular = new NormalDistribution(32.80, 17.09);
    private static final NormalDistribution distVolPesado = new NormalDistribution(230.52, 102.21);
    private static final ExponentialDistribution distInterArriboParticular = new ExponentialDistribution(4.37);
    private static final ExponentialDistribution distInterArriboPesado = new ExponentialDistribution(37.61);

    /**
     * Genera el volumen requerido por un vehículo particular usando una distribución Normal.
     * Cota inferior impuesta en 5.0 litros.
     * 
     * @return volumen en litros.
     */
    public static double generarVolumenParticular() {
        double muestra = distVolParticular.sample();
        return Math.max(5.0, muestra);
    }

    /**
     * Genera el volumen requerido por un vehículo de transporte pesado usando una distribución Normal.
     * Cota inferior impuesta en 20.0 litros.
     * 
     * @return volumen en litros.
     */
    public static double generarVolumenPesado() {
        double muestra = distVolPesado.sample();
        return Math.max(20.0, muestra);
    }

    /**
     * Genera el tiempo entre arribos para vehículos particulares usando una distribución Exponencial.
     * 
     * @return tiempo en minutos.
     */
    public static double generarInterArriboParticular() {
        return distInterArriboParticular.sample();
    }

    /**
     * Genera el tiempo entre arribos para vehículos de transporte pesado usando una distribución Exponencial.
     * 
     * @return tiempo en minutos.
     */
    public static double generarInterArriboPesado() {
        return distInterArriboPesado.sample();
    }
}
