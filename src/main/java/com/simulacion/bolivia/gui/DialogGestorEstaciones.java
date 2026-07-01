package com.simulacion.bolivia.gui;

import com.simulacion.bolivia.models.EstacionServicio;
import com.simulacion.bolivia.models.Surtidor;
import com.simulacion.bolivia.models.TanqueCombustible;
import javax.swing.*;
import java.awt.*;

/**
 * Diálogo secundario para la creación y parametrización de nuevas estaciones de servicio.
 */
public class DialogGestorEstaciones extends JDialog {
    private JTextField txtNombre;
    private JTextField txtApertura;
    private JTextField txtCierre;
    private JCheckBox chkGasolina;
    private JCheckBox chkDiesel;
    private JTextField txtCapacidad;
    private JTextField txtDistancia;
    private JTextField txtBombas;
    private JButton btnAgregar;

    private final double precioGasolinaDefault;
    private EstacionServicio estacionCreada = null;

    public DialogGestorEstaciones(Frame parent, double precioGasolinaDefault) {
        super(parent, "Configurar Nueva Estación", true);
        this.precioGasolinaDefault = precioGasolinaDefault;

        // Configuración del JDialog
        setSize(320, 420);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(9, 2, 10, 15));
        getContentPane().setBackground(Color.WHITE);

        // Campos de texto y componentes
        txtNombre = new JTextField("Estación B");
        txtApertura = new JTextField("6.0");
        txtCierre = new JTextField("22.0");
        chkGasolina = new JCheckBox("Vende Gasolina", true);
        chkGasolina.setBackground(Color.WHITE);
        chkDiesel = new JCheckBox("Vende Diésel", true);
        chkDiesel.setBackground(Color.WHITE);
        txtCapacidad = new JTextField("15000");
        txtDistancia = new JTextField("5.0");
        txtBombas = new JTextField("2");

        btnAgregar = new JButton("Agregar a la Red") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isEnabled()) {
                    g2.setColor(getBackground());
                } else {
                    g2.setColor(Color.LIGHT_GRAY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnAgregar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnAgregar.setBackground(new Color(40, 167, 69)); // Verde
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setContentAreaFilled(false);
        btnAgregar.setBorderPainted(false);
        btnAgregar.setFocusPainted(false);

        // Añadir componentes
        add(new JLabel(" Nombre Estación:"));
        add(txtNombre);

        add(new JLabel(" Hora Apertura (0-24):"));
        add(txtApertura);

        add(new JLabel(" Hora Cierre (0-24):"));
        add(txtCierre);

        add(new JLabel(" Combustibles:"));
        JPanel panelCheck = new JPanel(new GridLayout(1, 2));
        panelCheck.setBackground(Color.WHITE);
        panelCheck.add(chkGasolina);
        panelCheck.add(chkDiesel);
        add(panelCheck);

        add(new JLabel(" Capacidad Tanque (L):"));
        add(txtCapacidad);

        add(new JLabel(" Distancia (Km):"));
        add(txtDistancia);

        add(new JLabel(" Número de Bombas:"));
        add(txtBombas);

        add(new JLabel("")); // Relleno
        add(btnAgregar);

        // Eventos
        btnAgregar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                double horaApe = Double.parseDouble(txtApertura.getText().trim());
                double horaCie = Double.parseDouble(txtCierre.getText().trim());
                boolean vendeGas = chkGasolina.isSelected();
                boolean vendeDie = chkDiesel.isSelected();
                double cap = Double.parseDouble(txtCapacidad.getText().trim());
                double dist = Double.parseDouble(txtDistancia.getText().trim());
                int numBombas = Integer.parseInt(txtBombas.getText().trim());

                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El nombre de la estación no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!vendeGas && !vendeDie) {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar al menos un tipo de combustible a vender.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Crear los tanques asociados
                TanqueCombustible tg = vendeGas ? new TanqueCombustible("Gasolina Especial", cap, cap) : null;
                TanqueCombustible td = vendeDie ? new TanqueCombustible("Diésel", cap, cap) : null;

                // Crear la EstacionServicio
                estacionCreada = new EstacionServicio(nombre, horaApe, horaCie, vendeGas, vendeDie, tg, td);
                estacionCreada.setDistanciaKm(dist);

                // Crear los surtidores subvencionados asignados
                for (int i = 1; i <= numBombas; i++) {
                    // Asignar por defecto el tanque de gasolina si existe, sino el de diésel
                    TanqueCombustible tanqueAsignado = tg != null ? tg : td;
                    Surtidor s = new Surtidor("S" + i + "_" + nombre.replaceAll("\\s+", ""), "Subvencionado", precioGasolinaDefault, tanqueAsignado);
                    estacionCreada.getSurtidores().add(s);
                }

                dispose(); // Cerrar diálogo
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Introduce valores numéricos válidos en horas, capacidad, distancia y bombas.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public EstacionServicio getEstacionCreada() {
        return estacionCreada;
    }
}
