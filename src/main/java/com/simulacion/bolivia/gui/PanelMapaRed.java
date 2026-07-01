package com.simulacion.bolivia.gui;

import com.simulacion.bolivia.engine.MotorSimulacion;
import com.simulacion.bolivia.models.EstacionServicio;
import com.simulacion.bolivia.models.Surtidor;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Lienzo 2D que renderiza gráficamente el mapa de la red de estaciones de servicio,
 * los surtidores internos, las colas de vehículos y las líneas de distancia en Km.
 */
public class PanelMapaRed extends JPanel {
    private final MotorSimulacion motor;

    public PanelMapaRed(MotorSimulacion motor) {
        this.motor = motor;
        setBackground(new Color(240, 245, 250)); // Color de fondo tipo asfalto/suave
        setPreferredSize(new Dimension(950, 1000));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Habilitar antialiasing para trazos más definidos y suaves
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<EstacionServicio> estaciones = motor.getRedEstaciones();
        if (estaciones == null) {
            return;
        }

        // 1. Coordenadas de la Estación Internacional (derecha)
        int xInt = 700;
        int yInt = 80;
        int wInt = 200;
        int hInt = 120;
        int centerIntX = xInt + wInt / 2;
        int centerIntY = yInt + hInt / 2;

        // 2. Dibujar líneas de conexión y distancias desde las estaciones subvencionadas a la internacional
        int startX = 50;
        int startY = 80;
        int wEst = 300;
        int hEst = 120;

        for (int i = 0; i < estaciones.size(); i++) {
            EstacionServicio estacion = estaciones.get(i);
            int xEst = startX;
            int yEst = startY + i * 400; // Incrementado a 400 para evitar superposiciones
            int centerEstX = xEst + wEst / 2;
            int centerEstY = yEst + hEst / 2;

            // Determinar las coordenadas del destino de la conexión
            int targetX = centerIntX;
            int targetY = centerIntY;
            String conectadoA = estacion.getConectadoA();

            if (conectadoA != null && !"Internacional".equalsIgnoreCase(conectadoA)) {
                // Buscar la posición de la estación destino para conectar
                for (int j = 0; j < estaciones.size(); j++) {
                    EstacionServicio targetEst = estaciones.get(j);
                    if (targetEst.getNombre().equalsIgnoreCase(conectadoA)) {
                        int xTarget = startX;
                        int yTarget = startY + j * 400;
                        targetX = xTarget + wEst / 2;
                        targetY = yTarget + hEst / 2;
                        break;
                    }
                }
            }

            // Dibujar la línea de distancia
            g2d.setColor(new Color(180, 190, 200));
            g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{9}, 0)); // Línea punteada
            g2d.drawLine(centerEstX, centerEstY, targetX, targetY);

            // Dibujar etiqueta de distancia sobre la línea
            int labelX = (centerEstX + targetX) / 2 - 20;
            int labelY = (centerEstY + targetY) / 2 - 8;
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(labelX - 5, labelY - 12, 60, 18, 5, 5);
            g2d.setColor(new Color(108, 117, 125));
            g2d.drawRoundRect(labelX - 5, labelY - 12, 60, 18, 5, 5);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
            g2d.drawString(String.format("%.1f Km", estacion.getDistanciaKm()), labelX, labelY + 1);
        }

        // 3. Dibujar la Estación Internacional (Contornos oscuros para que resalten)
        List<Surtidor> surtidoresInt = motor.getEstacionInternacional().getSurtidores();
        int pumpWInt = 50;
        int pumpHInt = 40;
        int gapInt = 15;
        int startPumpXInt = xInt + 15;

        // Calcular ancho dinámico de la estación internacional en función del número de surtidores
        wInt = Math.max(200, surtidoresInt.size() * (pumpWInt + gapInt) + 15);

        g2d.setColor(new Color(230, 235, 240));
        g2d.fillRoundRect(xInt, yInt, wInt, hInt, 15, 15);
        g2d.setColor(new Color(52, 58, 64)); // Gris oscuro/asfalto oscuro
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawRoundRect(xInt, yInt, wInt, hInt, 15, 15);

        g2d.setColor(new Color(52, 58, 64));
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2d.drawString(motor.getNombreInternacional(), xInt + 15, yInt + 25);

        for (int j = 0; j < surtidoresInt.size(); j++) {
            Surtidor s = surtidoresInt.get(j);
            int px = startPumpXInt + j * (pumpWInt + gapInt);
            int py = yInt + 45;

            Color colorEstado = s.isEstaOcupado() ? new Color(220, 53, 69) : new Color(40, 167, 69);
            g2d.setColor(colorEstado);
            g2d.fillRoundRect(px, py, pumpWInt, pumpHInt, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.drawRoundRect(px, py, pumpWInt, pumpHInt, 8, 8);
            
            String label = s.getId();
            if (label.contains("_")) {
                label = label.split("_")[0];
            }
            g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
            int idW = g2d.getFontMetrics().stringWidth(label);
            g2d.drawString(label, px + 25 - idW / 2, py + 24);

            // Cola de la bomba Internacional (Máximo 7 autos)
            int numAutos = s.getTamanoFila();
            int maxAutosVisibles = 7;
            int autosADibujar = Math.min(numAutos, maxAutosVisibles);
            for (int k = 0; k < autosADibujar; k++) {
                int carX = px + 9; // Centrado respecto al surtidor
                int carY = yInt + hInt + 15 + (k * 30);
                dibujarVehiculo(g2d, carX, carY);
            }

            // Etiqueta de desbordamiento (Overflow Badge)
            if (numAutos > maxAutosVisibles) {
                int extra = numAutos - maxAutosVisibles;
                int badgeY = yInt + hInt + 15 + (maxAutosVisibles * 30) + 5;
                g2d.setColor(new Color(220, 53, 69)); // Rojo alerta
                g2d.fillRoundRect(px - 5, badgeY, 60, 20, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 11));
                String txtExtra = "+" + extra;
                int txtW = g2d.getFontMetrics().stringWidth(txtExtra);
                g2d.drawString(txtExtra, px + 25 - txtW / 2, badgeY + 14);
            }
        }

        // 4. Dibujar las Estaciones Subvencionadas y sus componentes
        for (int i = 0; i < estaciones.size(); i++) {
            EstacionServicio estacion = estaciones.get(i);
            int xEst = startX;
            int yEst = startY + i * 400; // Incrementado a 400

            // Dibujar Caja Grande de la Estación (Contornos oscuros para resaltar)
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(xEst, yEst, wEst, hEst, 15, 15);
            g2d.setColor(new Color(52, 58, 64)); // Gris oscuro/asfalto oscuro
            g2d.setStroke(new BasicStroke(2.5f));
            g2d.drawRoundRect(xEst, yEst, wEst, hEst, 15, 15);

            // Titulo de la Estación
            g2d.setColor(new Color(0, 123, 255)); // Azul premium
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.drawString(estacion.getNombre(), xEst + 15, yEst + 25);

            // Surtidores internos de esta estación
            List<Surtidor> surtidores = estacion.getSurtidores();
            int pumpW = 50;
            int pumpH = 40;
            int gap = 15;
            int startPumpX = xEst + 15;

            for (int j = 0; j < surtidores.size(); j++) {
                Surtidor s = surtidores.get(j);
                int px = startPumpX + j * (pumpW + gap);
                int py = yEst + 45;

                Color colorEstado = s.isEstaOcupado() ? new Color(220, 53, 69) : new Color(40, 167, 69);
                g2d.setColor(colorEstado);
                g2d.fillRoundRect(px, py, pumpW, pumpH, 8, 8);
                g2d.setColor(Color.WHITE);
                g2d.drawRoundRect(px, py, pumpW, pumpH, 8, 8);
                
                // Extraer el índice corto del surtidor (ej: S1)
                String label = s.getId();
                if (label.contains("_")) {
                    label = label.split("_")[0];
                }
                g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
                g2d.drawString(label, px + 17, py + 24);

                // Dibujar Cola de Vehículos (Máximo 7 autos)
                int numAutos = s.getTamanoFila();
                int maxAutosVisibles = 7;
                int autosADibujar = Math.min(numAutos, maxAutosVisibles);
                for (int k = 0; k < autosADibujar; k++) {
                    int carX = px + 9; // Centrado respecto a la bomba
                    int carY = yEst + hEst + 15 + (k * 30);
                    dibujarVehiculo(g2d, carX, carY);
                }

                // Etiqueta de desbordamiento (Overflow Badge)
                if (numAutos > maxAutosVisibles) {
                    int extra = numAutos - maxAutosVisibles;
                    int badgeY = yEst + hEst + 15 + (maxAutosVisibles * 30) + 5;
                    g2d.setColor(new Color(220, 53, 69)); // Rojo alerta
                    g2d.fillRoundRect(px - 5, badgeY, 60, 20, 10, 10);
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 11));
                    String txtExtra = "+" + extra;
                    int txtW = g2d.getFontMetrics().stringWidth(txtExtra);
                    g2d.drawString(txtExtra, px + 25 - txtW / 2, badgeY + 14);
                }
            }
        }
    }

    private void dibujarVehiculo(Graphics2D g2d, int x, int y) {
        int w = 32;
        int h = 12;
        
        // Cuerpo del Vehículo (Un poco más pequeño/estilizado)
        g2d.setColor(new Color(0, 123, 255)); // Azul
        g2d.fillRoundRect(x, y, w, h, 4, 4);
        g2d.setColor(new Color(0, 80, 180));
        g2d.drawRoundRect(x, y, w, h, 4, 4);

        // Ventana
        g2d.setColor(Color.WHITE);
        g2d.fillRect(x + 20, y + 2, 6, 8);

        // Llantas
        g2d.setColor(Color.BLACK);
        g2d.fillOval(x + 5, y - 2, 6, 3);
        g2d.fillOval(x + 20, y - 2, 6, 3);
        g2d.fillOval(x + 5, y + h - 1, 6, 3);
        g2d.fillOval(x + 20, y + h - 1, 6, 3);
    }
}
