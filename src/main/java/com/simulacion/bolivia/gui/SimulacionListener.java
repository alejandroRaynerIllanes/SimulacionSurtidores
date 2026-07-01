package com.simulacion.bolivia.gui;

/**
 * Interfaz de comunicación entre el motor de simulación y la interfaz gráfica.
 */
public interface SimulacionListener {
    /**
     * Se invoca cada vez que cambia el estado interno del motor de simulación.
     */
    void onEstadoActualizado();
}
