package com.simulacion.bolivia.gui;

import com.simulacion.bolivia.models.Surtidor;
import javax.swing.*;
import java.awt.*;

/**
 * Panel que dibuja la representación gráfica de un surtidor y su cola de espera.
 */
public class PanelSurtidor extends JPanel {
    private final Surtidor surtidor;

    public PanelSurtidor(Surtidor surtidor) {
        this.surtidor = surtidor;
        // Establecer un tamaño mínimo/preferido sugerido para una buena visualización en grid
        setPreferredSize(new Dimension(140, 400));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.LIGHT_GRAY));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Habilitar antialiasing para un dibujo más suave
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Dibujar el surtidor (la bomba física)
        int pumpW = 70;
        int pumpH = 80;
        int pumpX = (width - pumpW) / 2;
        int pumpY = 30;

        // Color verde si está libre, rojo si está ocupado
        Color estadoColor = surtidor.isEstaOcupado() ? new Color(220, 53, 69) : new Color(40, 167, 69);
        
        // Dibujar cuerpo del surtidor
        g2.setColor(estadoColor);
        g2.fillRoundRect(pumpX, pumpY, pumpW, pumpH, 15, 15);
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(pumpX, pumpY, pumpW, pumpH, 15, 15);

        // Pantalla indicadora interna de la bomba
        g2.setColor(Color.BLACK);
        g2.fillRect(pumpX + 10, pumpY + 15, pumpW - 20, 20);
        g2.setColor(Color.GREEN);
        g2.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2.drawString("L/min: " + (int)surtidor.getCaudalLitrosPorMinuto(), pumpX + 12, pumpY + 28);

        // 2. Textos informativos de identificación
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        
        String txtId = "Surtidor " + surtidor.getId();
        g2.drawString(txtId, (width - fm.stringWidth(txtId)) / 2, pumpY - 12);
        
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        fm = g2.getFontMetrics();
        String txtTipo = surtidor.getTipo();
        g2.drawString(txtTipo, (width - fm.stringWidth(txtTipo)) / 2, pumpY - 2);

        // 3. Dibujar la cola de vehículos (autos esperando) debajo del surtidor
        int numVehiculos = surtidor.getTamanoFila();
        
        // Espacio para la cola
        int startQueueY = pumpY + pumpH + 30;
        int autoW = 40;
        int autoH = 20;
        int spacing = 8;

        for (int i = 0; i < numVehiculos; i++) {
            int autoY = startQueueY + i * (autoH + spacing);
            
            // Si el dibujo se sale del panel, mostramos un indicador de "+ X autos" en lugar de pintar indefinidamente
            if (autoY + autoH > height - 20) {
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                String masAutos = "+ " + (numVehiculos - i) + " colados";
                fm = g2.getFontMetrics();
                g2.drawString(masAutos, (width - fm.stringWidth(masAutos)) / 2, autoY + 10);
                break;
            }

            // Dibujar auto como un rectángulo redondeado de color azul/celeste
            g2.setColor(new Color(0, 123, 255));
            int autoX = (width - autoW) / 2;
            g2.fillRoundRect(autoX, autoY, autoW, autoH, 6, 6);
            
            // Borde
            g2.setColor(new Color(0, 80, 180));
            g2.drawRoundRect(autoX, autoY, autoW, autoH, 6, 6);
            
            // Ventanita
            g2.setColor(Color.WHITE);
            g2.fillRect(autoX + 25, autoY + 3, 8, 14);

            // Ruedas
            g2.setColor(Color.BLACK);
            g2.fillOval(autoX + 6, autoY - 2, 8, 4);
            g2.fillOval(autoX + 24, autoY - 2, 8, 4);
            g2.fillOval(autoX + 6, autoY + autoH - 2, 8, 4);
            g2.fillOval(autoX + 24, autoY + autoH - 2, 8, 4);
        }

        // Si la fila está vacía, pintar un texto indicativo suave
        if (numVehiculos == 0) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("SansSerif", Font.ITALIC, 11));
            fm = g2.getFontMetrics();
            String vacioStr = "Sin cola";
            g2.drawString(vacioStr, (width - fm.stringWidth(vacioStr)) / 2, startQueueY + 15);
        }
    }
}
