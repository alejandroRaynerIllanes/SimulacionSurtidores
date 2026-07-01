package com.simulacion.bolivia.gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase de arranque para la aplicación de simulación con interfaz gráfica.
 */
public class AppGrafica {
    public static void main(String[] args) {
        // Intentar usar el Look and Feel nativo del sistema para una apariencia premium
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignorar y usar el Look and Feel por defecto si falla
        }

        // Lanzar la GUI en el hilo de despacho de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
