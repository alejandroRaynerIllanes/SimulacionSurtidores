package com.simulacion.bolivia.app;

import com.simulacion.bolivia.engine.MotorSimulacion;

/**
 * Clase principal que arranca la simulación de eventos discretos.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando Simulación de Estación de Servicio en Bolivia...");
        
        MotorSimulacion motor = new MotorSimulacion();
        
        // Inicializar el sistema (tanques, surtidores y primeros arribos)
        motor.inicializarSistema(null);
        
        // Ejecutar simulación para 90 días (129,600 minutos)
        // 90 días * 24 horas/día * 60 minutos/hora = 129600 minutos
        double tiempoLimite = 129600.0;
        motor.ejecutarSimulacion(tiempoLimite);
        
        // Imprimir el reporte final consolidado de KPIs y monetización
        motor.generarReporteFinal();
    }
}
