package com.simulacion.bolivia.gui;

import com.simulacion.bolivia.engine.MotorSimulacion;
import com.simulacion.bolivia.models.EstacionServicio;
import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal para la configuración de la Estación Internacional y la gestión de sus surtidores.
 */
public class DialogConfiguracionInternacional extends JDialog {
    private JTextField txtNombre;
    private JTextField txtCapacidad;
    private JTextField txtSurtidoresCount;
    private JButton btnPersonalizarSurtidores;
    private MotorSimulacion motor;

    public DialogConfiguracionInternacional(JDialog owner, MotorSimulacion motor) {
        super(owner, "Configurar Estación Internacional", true);
        this.motor = motor;

        setSize(450, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 15));
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        EstacionServicio estInt = motor.getEstacionInternacional();

        txtNombre = new JTextField(motor.getNombreInternacional());
        txtCapacidad = new JTextField(String.valueOf(motor.getCapacidadInternacional()));
        
        txtSurtidoresCount = new JTextField(String.valueOf(estInt.getSurtidores().size()));
        txtSurtidoresCount.setEditable(false);
        txtSurtidoresCount.setBackground(new Color(245, 245, 245));

        JPanel panelSurtidorRow = new JPanel(new BorderLayout(10, 0));
        panelSurtidorRow.setBackground(Color.WHITE);
        panelSurtidorRow.add(txtSurtidoresCount, BorderLayout.CENTER);

        btnPersonalizarSurtidores = new JButton("⚙️ Surtidores...") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnPersonalizarSurtidores.setBackground(new Color(108, 117, 125)); // Gris
        btnPersonalizarSurtidores.setForeground(Color.WHITE);
        btnPersonalizarSurtidores.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnPersonalizarSurtidores.setContentAreaFilled(false);
        btnPersonalizarSurtidores.setBorderPainted(false);
        btnPersonalizarSurtidores.setFocusPainted(false);
        panelSurtidorRow.add(btnPersonalizarSurtidores, BorderLayout.EAST);

        panelForm.add(new JLabel("Nombre Estación:"));
        panelForm.add(txtNombre);

        panelForm.add(new JLabel("Capacidad Tanque (L):"));
        panelForm.add(txtCapacidad);

        panelForm.add(new JLabel("Surtidores Activos:"));
        panelForm.add(panelSurtidorRow);

        add(panelForm, BorderLayout.CENTER);

        // Acción del botón personalizar
        btnPersonalizarSurtidores.addActionListener(e -> {
            DialogGestorSurtidores dialogSurtidores = new DialogGestorSurtidores(this, estInt);
            dialogSurtidores.setVisible(true);
            // Actualizar contador
            txtSurtidoresCount.setText(String.valueOf(estInt.getSurtidores().size()));
        });

        JButton btnGuardar = new JButton("💾 Guardar Cambios") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnGuardar.setBackground(new Color(40, 167, 69)); // Verde
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnGuardar.setContentAreaFilled(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);

        btnGuardar.addActionListener(e -> {
            try {
                String nom = txtNombre.getText().trim();
                double cap = Double.parseDouble(txtCapacidad.getText().trim());

                if (nom.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (estInt.getSurtidores().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe configurar al menos un surtidor para la estación internacional.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Guardar datos globales en el motor
                motor.setNombreInternacional(nom);
                motor.setCapacidadInternacional(cap);

                // Sincronizar el objeto EstacionServicio interno
                estInt.setNombre(nom);
                if (estInt.getTanqueGasolina() != null) {
                    estInt.getTanqueGasolina().setCapacidadMaxima(cap);
                    estInt.getTanqueGasolina().setNivelActual(cap);
                }

                JOptionPane.showMessageDialog(this, "Estación Internacional configurada con éxito.", "Guardado", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, introduce valores numéricos válidos en capacidad.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSur.setBackground(Color.WHITE);
        panelSur.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        panelSur.add(btnGuardar);
        add(panelSur, BorderLayout.SOUTH);
    }
}
