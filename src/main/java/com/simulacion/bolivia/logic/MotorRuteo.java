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
    public static Surtidor elegirMejorSurtidor(Vehiculo vehiculo, List<com.simulacion.bolivia.models.EstacionServicio> redEstaciones, Surtidor surtidorInternacional, double precioGasolina, double precioDiesel, double precioInt, double reloj) {
        // Los extranjeros no gozan de subvención y van directo al internacional
        if (vehiculo.isEsExtranjero()) {
            return surtidorInternacional;
        }

        if (redEstaciones == null || redEstaciones.isEmpty()) {
            return null;
        }

        // 1. Buscar el surtidor subvencionado con menor fila entre todas las estaciones abiertas y válidas
        Surtidor mejorSurtidorSub = null;
        for (com.simulacion.bolivia.models.EstacionServicio estacion : redEstaciones) {
            // Verificar si la estación está abierta a esta hora de la simulación
            if (!estacion.estaAbierta(reloj)) {
                continue;
            }
            
            // Verificar si ofrece el combustible requerido para el perfil
            if ("Particular".equalsIgnoreCase(vehiculo.getPerfil()) && !estacion.isVendeGasolina()) {
                continue;
            }
            if ("Transporte Pesado".equalsIgnoreCase(vehiculo.getPerfil()) && !estacion.isVendeDiesel()) {
                continue;
            }

            // Buscar en los surtidores de esta estación
            for (Surtidor s : estacion.getSurtidores()) {
                if (mejorSurtidorSub == null || s.getTamanoFila() < mejorSurtidorSub.getTamanoFila()) {
                    mejorSurtidorSub = s;
                }
            }
        }

        // Si ninguna estación válida está abierta o disponible, el vehículo se va sin cargar (retorna null)
        if (mejorSurtidorSub == null) {
            return null;
        }

        // 2. Buscar la estación a la que pertenece mejorSurtidorSub para obtener su distancia
        com.simulacion.bolivia.models.EstacionServicio estacionAsociada = null;
        for (com.simulacion.bolivia.models.EstacionServicio es : redEstaciones) {
            if (es.getSurtidores().contains(mejorSurtidorSub)) {
                estacionAsociada = es;
                break;
            }
        }

        // Asumiremos velocidad de 30 Km/h. Tiempo de ida y vuelta en minutos:
        double tiempoViajeMinutos = 0.0;
        if (estacionAsociada != null) {
            tiempoViajeMinutos = (estacionAsociada.getDistanciaKm() / 30.0) * 60.0 * 2.0;
        }

        // 3. Calcular la espera estimada en el mejor surtidor subvencionado encontrado
        double esperaEstimadaSub = calcularTiempoEsperaEstimado(mejorSurtidorSub, vehiculo);

        // 4. Calcular Costo Total Subvencionado (CT_sub) (tiempo perdido = viaje + cola)
        double precioAplicado = "Particular".equalsIgnoreCase(vehiculo.getPerfil()) ? precioGasolina : precioDiesel;
        double costoCombustibleSub = vehiculo.getVolumenRequerido() * precioAplicado;
        double costoEsperaSub = ((esperaEstimadaSub + tiempoViajeMinutos) / 60.0) * vehiculo.getCostoOportunidadHora();
        double CT_sub = costoCombustibleSub + costoEsperaSub;

        // 5. Calcular Costo Total Internacional (CT_int) -> Se asume espera en cola 0 (tiempo perdido = viaje)
        double costoCombustibleInt = vehiculo.getVolumenRequerido() * precioInt;
        double costoEsperaInt = (tiempoViajeMinutos / 60.0) * vehiculo.getCostoOportunidadHora();
        double CT_int = costoCombustibleInt + costoEsperaInt;

        // 6. Retornar el surtidor internacional si CT_int <= CT_sub, de lo contrario el subvencionado
        if (CT_int <= CT_sub) {
            return surtidorInternacional;
        } else {
            return mejorSurtidorSub;
        }
    }
}
