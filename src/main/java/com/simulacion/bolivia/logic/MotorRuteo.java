package com.simulacion.bolivia.logic;

import com.simulacion.bolivia.models.Surtidor;
import com.simulacion.bolivia.models.Vehiculo;
import java.util.List;

/**
 * Clase que gestiona el enrutamiento inteligente de los vehículos en la estación de servicio.
 * Compara costos totales considerando el precio del combustible y el costo de oportunidad del tiempo de espera.
 */
public class MotorRuteo {

    /**
     * Estima el tiempo de espera en minutos para un vehículo determinado en un surtidor específico.
     * Si el surtidor está vacío (no hay fila), la espera es 0.
     * Si hay fila, estima un promedio de servicio según el perfil (2 minutos para Particular, 5 minutos para Pesado).
     * 
     * @param surtidor surtidor en el que se evalúa la espera.
     * @param vehiculo vehículo para el cual se realiza la estimación.
     * @return tiempo de espera estimado en minutos.
     */
    public static double calcularTiempoEsperaEstimado(Surtidor surtidor, Vehiculo vehiculo) {
        if (surtidor.getTamanoFila() == 0) {
            return 0.0;
        }
        double tiempoServicioPromedio = "Particular".equalsIgnoreCase(vehiculo.getPerfil()) ? 2.0 : 5.0;
        return surtidor.getTamanoFila() * tiempoServicioPromedio;
    }

    /**
     * Elige el surtidor óptimo para el vehículo en base al menor costo total (Combustible + Costo Oportunidad de Espera).
     * Compara el mejor surtidor subvencionado (el de menor fila) con el surtidor internacional (donde se asume espera 0).
     * 
     * @param vehiculo vehículo a enrutar.
     * @param surtidoresSubvencionados lista de surtidores que despachan a precio subvencionado.
     * @param surtidorInternacional surtidor que despacha a precio internacional (sin fila/espera 0).
     * @param precioSubvencionadoAct precio subvencionado actual por litro.
     * @return el surtidor óptimo seleccionado.
     */
    public static Surtidor elegirMejorSurtidor(Vehiculo vehiculo, List<Surtidor> surtidoresSubvencionados, Surtidor surtidorInternacional, double precioSubvencionadoAct) {
        if (surtidoresSubvencionados == null || surtidoresSubvencionados.isEmpty()) {
            return surtidorInternacional;
        }

        // 1. Buscar el surtidor con menor fila de espera entre los subvencionados
        Surtidor mejorSurtidorSub = surtidoresSubvencionados.get(0);
        for (Surtidor s : surtidoresSubvencionados) {
            if (s.getTamanoFila() < mejorSurtidorSub.getTamanoFila()) {
                mejorSurtidorSub = s;
            }
        }

        // 2. Calcular la espera estimada en el mejor surtidor subvencionado
        double esperaEstimadaSub = calcularTiempoEsperaEstimado(mejorSurtidorSub, vehiculo);

        // 3. Calcular Costo Total Subvencionado (CT_sub)
        // CT_sub = (volumen * precioSub) + ((esperaMinutos / 60) * costoOportunidadHora)
        double costoCombustibleSub = vehiculo.getVolumenRequerido() * precioSubvencionadoAct;
        double costoEsperaSub = (esperaEstimadaSub / 60.0) * vehiculo.getCostoOportunidadHora();
        double CT_sub = costoCombustibleSub + costoEsperaSub;

        // 4. Calcular Costo Total Internacional (CT_int) -> Se asume espera 0
        double CT_int = vehiculo.getVolumenRequerido() * surtidorInternacional.getPrecioPorLitro();

        // 5. Retornar el surtidor internacional si CT_int <= CT_sub, de lo contrario el subvencionado
        if (CT_int <= CT_sub) {
            return surtidorInternacional;
        } else {
            return mejorSurtidorSub;
        }
    }
}
