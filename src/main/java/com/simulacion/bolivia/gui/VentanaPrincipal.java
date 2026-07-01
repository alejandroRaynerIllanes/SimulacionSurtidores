package com.simulacion.bolivia.gui;

import com.simulacion.bolivia.engine.MotorSimulacion;
import com.simulacion.bolivia.models.Surtidor;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal en Swing que representa la interfaz de usuario de la simulación.
 */
public class VentanaPrincipal extends JFrame implements SimulacionListener {
    private final MotorSimulacion motor;

    // Componentes del Dashboard (Norte)
    private JLabel lblReloj;
    private JLabel lblTanque;
    private JLabel lblAlarmaCisterna;

    // Componente de Mapa de Red (Centro)
    private PanelMapaRed mapaRed;

    // Componentes de Controles y KPIs (Sur)
    private JLabel lblVehiculosSub;
    private JLabel lblVehiculosInt;
    private JLabel lblIngresos;
    private JLabel lblEsperaMedia;
    private JButton btnIniciar;
    private JSlider sliderVelocidad;

    // Componentes del Panel What-If (Oeste)
    private JTextField txtPrecioGas;
    private JTextField txtPrecioDiesel;
    private JTextField txtPrecioInt;
    private JTextField txtCuotaGas;
    private JTextField txtCuotaDiesel;
    private JTextField txtDiasSim;
    private JButton btnConfigurarRed;
    private JLabel lblConteoEstaciones;

    private final List<com.simulacion.bolivia.models.EstacionServicio> estacionesUsuario = new java.util.ArrayList<>();
    private boolean simIniciada = false;

    public VentanaPrincipal() {
        // 1. Instanciar el motor
        this.motor = new MotorSimulacion();
        this.motor.setListener(this);

        // Inicializar vacío (solo mostrar Estación Internacional por defecto)
        this.motor.inicializarSistema(estacionesUsuario);

        // 2. Configurar el frame
        setTitle("Simulación Estación de Servicio - Bolivia (Trimestre 3)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 3. Crear secciones
        crearPanelNorte();
        crearPanelConfiguracion(); // Añade el panel What-If a la izquierda (WEST)
        crearPanelCentro();
        crearPanelSur();

        // Actualizar la interfaz inicial con el estado del sistema cargado
        onEstadoActualizado();
    }

    private void crearPanelNorte() {
        JPanel panelNorte = new JPanel(new GridLayout(1, 4, 10, 10));
        panelNorte.setBackground(new Color(40, 44, 52));
        panelNorte.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        lblReloj = new JLabel("Reloj: 0.00 min");
        lblReloj.setForeground(Color.WHITE);
        lblReloj.setFont(new Font("SansSerif", Font.BOLD, 12));

        lblTanque = new JLabel("Gasolina: 30000 L | Diésel: 30000 L");
        lblTanque.setForeground(Color.WHITE);
        lblTanque.setFont(new Font("SansSerif", Font.BOLD, 12));

        lblAlarmaCisterna = new JLabel("ESTADO T. SUBVENCIONADO: OK");
        lblAlarmaCisterna.setForeground(new Color(40, 167, 69)); // Verde
        lblAlarmaCisterna.setFont(new Font("SansSerif", Font.BOLD, 12));

        panelNorte.add(lblReloj);
        panelNorte.add(lblTanque);
        panelNorte.add(lblAlarmaCisterna);

        add(panelNorte, BorderLayout.NORTH);
    }

    private void crearPanelConfiguracion() {
        JPanel panelConfig = new JPanel(new GridLayout(7, 2, 10, 20));
        panelConfig.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Panel What-If"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panelConfig.setBackground(Color.WHITE);

        // Instanciar campos de texto
        txtPrecioGas = new JTextField("6.96");
        txtPrecioDiesel = new JTextField("9.80");
        txtPrecioInt = new JTextField("12.50");
        txtCuotaGas = new JTextField("100");
        txtCuotaDiesel = new JTextField("60");
        txtDiasSim = new JTextField("90");

        btnConfigurarRed = new JButton("⚙️ Configurar Red de Estaciones...");
        lblConteoEstaciones = new JLabel("Estaciones en red: 0");

        btnConfigurarRed.addActionListener(e -> {
            DialogGestorRed dialog = new DialogGestorRed(this, true, estacionesUsuario, motor);
            dialog.setVisible(true);
            // Sincronizar el motor con la red modificada inmediatamente
            motor.inicializarSistema(estacionesUsuario);
            lblConteoEstaciones.setText("Estaciones en red: " + estacionesUsuario.size());
            mapaRed.repaint(); // Actualizar el mapa inmediatamente
        });

        // Añadir componentes al panel
        panelConfig.add(new JLabel("Precio Gasolina:"));
        panelConfig.add(txtPrecioGas);
        
        panelConfig.add(new JLabel("Precio Diésel:"));
        panelConfig.add(txtPrecioDiesel);
        
        panelConfig.add(new JLabel("Precio Int.:"));
        panelConfig.add(txtPrecioInt);

        panelConfig.add(new JLabel("Cuota Gasolina (%):"));
        panelConfig.add(txtCuotaGas);

        panelConfig.add(new JLabel("Cuota Diésel (%):"));
        panelConfig.add(txtCuotaDiesel);

        panelConfig.add(new JLabel("Días a Simular:"));
        panelConfig.add(txtDiasSim);

        panelConfig.add(lblConteoEstaciones);
        panelConfig.add(btnConfigurarRed);

        // Envolver para evitar estiramiento vertical
        JPanel panelWhatIfWrapper = new JPanel(new BorderLayout());
        panelWhatIfWrapper.setBackground(Color.WHITE);
        panelWhatIfWrapper.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
        panelWhatIfWrapper.setPreferredSize(new Dimension(280, 0));
        panelWhatIfWrapper.add(panelConfig, BorderLayout.NORTH);

        add(panelWhatIfWrapper, BorderLayout.WEST);
    }

    private void crearPanelCentro() {
        mapaRed = new PanelMapaRed(motor);
        JScrollPane scroll = new JScrollPane(mapaRed);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(new Color(240, 245, 250));
        scroll.getViewport().setBackground(new Color(240, 245, 250));
        add(scroll, BorderLayout.CENTER);
    }

    private void crearPanelSur() {
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        panelSur.setBackground(Color.WHITE);

        // Panel de KPIs
        JPanel panelKPIs = new JPanel(new GridLayout(2, 2, 10, 5));
        panelKPIs.setBackground(Color.WHITE);

        lblVehiculosSub = new JLabel("Vehículos Subvencionados: 0");
        lblVehiculosSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblVehiculosInt = new JLabel("Vehículos Internacionales: 0");
        lblVehiculosInt.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblIngresos = new JLabel("Ingresos totales: 0.00 Bs.");
        lblIngresos.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEsperaMedia = new JLabel("Tiempo medio de espera: 0.00 min");
        lblEsperaMedia.setFont(new Font("SansSerif", Font.PLAIN, 12));

        panelKPIs.add(lblVehiculosSub);
        panelKPIs.add(lblVehiculosInt);
        panelKPIs.add(lblIngresos);
        panelKPIs.add(lblEsperaMedia);

        panelSur.add(panelKPIs, BorderLayout.CENTER);

        // Panel de Controles
        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        panelControles.setBackground(Color.WHITE);

        // Control de velocidad (delay ms)
        sliderVelocidad = new JSlider(JSlider.HORIZONTAL, 0, 500, 50);
        sliderVelocidad.setMajorTickSpacing(100);
        sliderVelocidad.setPaintTicks(true);
        sliderVelocidad.setPreferredSize(new Dimension(150, 45));
        sliderVelocidad.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(), "Retardo (ms)",
                0, 0, new Font("SansSerif", Font.PLAIN, 9)
        ));
        sliderVelocidad.addChangeListener(e -> {
            int delay = sliderVelocidad.getValue();
            motor.setVelocidadDelayMs(delay);
        });

        btnIniciar = new JButton("Iniciar Simulación") {
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
        btnIniciar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnIniciar.setBackground(new Color(0, 123, 255));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setContentAreaFilled(false);
        btnIniciar.setBorderPainted(false);
        btnIniciar.setFocusPainted(false);
        
        btnIniciar.addActionListener(e -> {
            if (!simIniciada) {
                simIniciada = true;
                btnIniciar.setEnabled(false);
                btnIniciar.setText("Simulación en curso...");

                // Deshabilitar los campos del panel What-If
                txtPrecioGas.setEnabled(false);
                txtPrecioDiesel.setEnabled(false);
                txtPrecioInt.setEnabled(false);
                txtCuotaGas.setEnabled(false);
                txtCuotaDiesel.setEnabled(false);
                txtDiasSim.setEnabled(false);
                btnConfigurarRed.setEnabled(false);

                double tiempoLimiteMinutos = 129600.0; // default

                // Configurar parámetros leídos del panel de configuración What-If
                try {
                    motor.setPrecioGasolinaSubv(Double.parseDouble(txtPrecioGas.getText().trim()));
                    motor.setPrecioDieselSubv(Double.parseDouble(txtPrecioDiesel.getText().trim()));
                    motor.setPrecioInternacional(Double.parseDouble(txtPrecioInt.getText().trim()));
                    motor.setPorcentajeEntregaYpfbGas(Double.parseDouble(txtCuotaGas.getText().trim()));
                    motor.setPorcentajeEntregaYpfbDiesel(Double.parseDouble(txtCuotaDiesel.getText().trim()));
                    
                    double dias = Double.parseDouble(txtDiasSim.getText().trim());
                    tiempoLimiteMinutos = dias * 1440.0;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Por favor introduce valores numéricos válidos en el panel What-If.", 
                            "Error de Formato", JOptionPane.ERROR_MESSAGE);
                    // Re-habilitar para corregir error
                    simIniciada = false;
                    btnIniciar.setEnabled(true);
                    btnIniciar.setText("Iniciar Simulación");
                    txtPrecioGas.setEnabled(true);
                    txtPrecioDiesel.setEnabled(true);
                    txtPrecioInt.setEnabled(true);
                    txtCuotaGas.setEnabled(true);
                    txtCuotaDiesel.setEnabled(true);
                    txtDiasSim.setEnabled(true);
                    btnConfigurarRed.setEnabled(true);
                    return;
                }

                // Reinicializar el sistema con los nuevos parámetros antes de correr
                motor.inicializarSistema(estacionesUsuario);

                // Reconstruir los paneles de surtidores para usar las nuevas referencias
                SwingUtilities.invokeLater(() -> {
                    mapaRed.repaint();
                });

                // Ejecutar la simulación en un hilo secundario para no bloquear el EDT (Event Dispatch Thread)
                final double limite = tiempoLimiteMinutos;
                new Thread(() -> {
                    motor.ejecutarSimulacion(limite);
                    
                    // Al finalizar, habilitar el botón y mostrar el reporte en consola
                    SwingUtilities.invokeLater(() -> {
                        btnIniciar.setText("Simulación Finalizada");
                        motor.generarReporteFinal();
                    });
                }).start();
            }
        });

        panelControles.add(sliderVelocidad);
        panelControles.add(btnIniciar);

        panelSur.add(panelControles, BorderLayout.EAST);

        add(panelSur, BorderLayout.SOUTH);
    }

    @Override
    public void onEstadoActualizado() {
        // Ejecutar actualizaciones de Swing de forma segura en el EDT
        SwingUtilities.invokeLater(() -> {
            double relojActual = motor.getReloj();
            
            // Convertir minutos a días, horas y minutos para una lectura más humana
            int dias = (int) (relojActual / 1440.0);
            int horas = (int) ((relojActual % 1440.0) / 60.0);
            int mins = (int) (relojActual % 60.0);
            
            lblReloj.setText(String.format("Reloj: Día %d (%02d:%02d) - [%.1f min]", dias + 1, horas, mins, relojActual));

            // Actualizar Tanques
            double nivelGas = motor.getTanqueGasolina().getNivelActual();
            double nivelDie = motor.getTanqueDiesel().getNivelActual();
            lblTanque.setText(String.format("Gasolina: %.1f L | Diésel: %.1f L", nivelGas, nivelDie));

            // Actualizar Alerta de Cisterna
            if (motor.isEsperandoCisterna()) {
                lblAlarmaCisterna.setText("!!! DESABASTECIDO - SOLICITADO CISNERNA !!!");
                lblAlarmaCisterna.setForeground(new Color(220, 53, 69)); // Rojo
            } else {
                lblAlarmaCisterna.setText("ESTADO T. SUBVENCIONADO: OK");
                lblAlarmaCisterna.setForeground(new Color(40, 167, 69)); // Verde
            }

            // Actualizar KPIs en la UI
            int atendidosSubv = motor.getVehiculosAtendidosSubv();
            int atendidosInt = motor.getVehiculosAtendidosInt();
            int totalAtendidos = atendidosSubv + atendidosInt;
            
            lblVehiculosSub.setText("Vehículos Subvencionados: " + atendidosSubv);
            lblVehiculosInt.setText("Vehículos Internacionales: " + atendidosInt);

            double ingresosSub = motor.getIngresosAcumuladosSubv();
            double ingresosInt = motor.getLitrosVendidosInt() * motor.getPrecioInternacional();
            double ingresosTotales = ingresosSub + ingresosInt;
            lblIngresos.setText(String.format("Ingresos totales: %.2f Bs.", ingresosTotales));

            double esperaMedia = totalAtendidos > 0 ? (motor.getTiempoEsperaTotal() / totalAtendidos) : 0.0;
            lblEsperaMedia.setText(String.format("Tiempo medio de espera: %.2f min", esperaMedia));

            // Repintar el lienzo del mapa de red
            mapaRed.repaint();
        });
    }
}
